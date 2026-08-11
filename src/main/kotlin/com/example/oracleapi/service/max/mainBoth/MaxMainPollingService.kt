package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.Helper
import com.example.oracleapi.config.MaxApiProperties
import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.agnphonenumber.AgnPhoneService
import com.example.oracleapi.service.ats.AsteriskService
import com.example.oracleapi.service.ats.CallNotificationService
import com.example.oracleapi.service.barcode.BarcodeService
import com.example.oracleapi.service.maxUserAgn.MaxUserAgnService
import com.example.oracleapi.service.nomnlist.NomnlistService
import com.example.oracleapi.util.PhoneUtils
import ezvcard.Ezvcard
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class MaxMainPollingService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties,
    private val botClient: MaxBotMainClient,
    private val agnPhoneService: AgnPhoneService,
    private val maxUserAgnService: MaxUserAgnService,
    private val barcodeService: BarcodeService,
    private val nomnlistService: NomnlistService,
    private val phonebookRepository: PhonebookRepository,
    private val asteriskService: AsteriskService,
    private val callNotificationService: CallNotificationService
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private var marker: Long? = null
    private val searchState = mutableMapOf<String, Boolean>()

    @Scheduled(fixedDelay = 2000, initialDelay = 5000)
    fun pollUpdates() {
        try {
            val uriBuilder = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/updates")
                .queryParam("limit", 10)
                .queryParam("timeout", 30)

            if (marker != null) {
                uriBuilder.queryParam("marker", marker)
            }

            val url = uriBuilder.build().toUri()

            val headers = HttpHeaders().apply {
                set("Authorization", properties.botMainToken)
            }

            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity<Nothing>(headers),
                Map::class.java
            )

            val body = response.body
            val updates = body?.get("updates") as? List<*> ?: return
            val newMarker = (body["marker"] as? Number)?.toLong()

            if (newMarker != null && newMarker != marker) {
                marker = newMarker
                log.info("🤖 [Main Bot] Получены новые обновления. Маркер: $marker")
            }

            updates.forEach { updateRaw ->
                val update = updateRaw as? Map<*, *> ?: return@forEach

                when (val updateType = update["update_type"] as? String) {
                    "bot_started" -> {
                        val userId = (update["user_id"] as? Number)?.toString() ?: return@forEach
                        val chatId = (update["chat_id"] as? Number)?.toString() ?: return@forEach
                        log.info("🚀 [Main Bot] Бот запущен: userId=$userId, chatId=$chatId")
                        sendMainMenu(chatId)
                    }

                    "message_created" -> {
                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val sender = message["sender"] as? Map<*, *>
                        val recipient = message["recipient"] as? Map<*, *>
                        val bodyMap = message["body"] as? Map<*, *>

                        val userId = (sender?.get("user_id") as? Number)?.toString() ?: return@forEach
                        val chatId = (recipient?.get("chat_id") as? Number)?.toString() ?: return@forEach
                        val text = bodyMap?.get("text") as? String

                        // ✅ Флаг: было ли обработано вложение
                        var attachmentProcessed = false

                        // Проверяем вложения
                        val attachments = bodyMap?.get("attachments") as? List<*>
                        attachments?.forEach { attachment ->
                            val attachmentMap = attachment as? Map<*, *>
                            when (attachmentMap?.get("type")) {
                                "contact" -> {
                                    val payload = attachmentMap["payload"] as? Map<*, *>
                                    val vcfInfo = payload?.get("vcf_info") as? String
                                    log.info("📱 [Main Bot] Получен контакт: $vcfInfo")

                                    if (vcfInfo != null) {
                                        val contactInfo = parseContactFromVcf(vcfInfo)
                                        if (contactInfo != null) {
                                            handlePhoneNumberReceived(userId, chatId, contactInfo)
                                        }
                                    }
                                    attachmentProcessed = true
                                }

                                "image" -> {
                                    log.info("📸 [Main Bot] Обработка изображения...")
                                    attachmentProcessed = true  // ← Сразу ставим
                                    val payload = attachmentMap["payload"] as? Map<*, *>
                                    val photoUrl = payload?.get("url") as? String
                                    val photoToken = payload?.get("token") as? String

                                    log.info("📸 [Main Bot] Получено ФОТО от user_id=$userId, chatId=$chatId")
                                    log.info("📸 [Main Bot] URL: $photoUrl")
                                    log.info("📸 [Main Bot] TOKEN: $photoToken")

                                    if (photoUrl != null) {
                                        val authToken = "Bearer ${properties.botMainToken}"
                                        val barcode = barcodeService.decodeBarcodeFromUrlWithAuth(photoUrl, authToken)
                                        if (barcode != null) {
                                            log.info("📱 [Main Bot] Распознан код: $barcode")
                                            val nomen = nomnlistService.findByBarcode(barcode)

                                            val article = Helper.insertSpaceToArticle(nomen?.article ?: "не найден")

                                            val nomenText = """
                                                ${nomen?.nomenname ?: "Товар со штрих-кодом $barcode не найден"}
                                                Артикул: $article
                                            """.trimIndent()

                                            botClient.sendMessage(chatId, nomenText, "markdown")
                                        } else {
                                            botClient.sendMessage(chatId, "❌ Не удалось распознать код на фото", "markdown")
                                        }
                                    } else {
                                        botClient.sendMessage(chatId, "📸 *Фото получено*", "markdown")
                                    }
                                }
                            }
                        }

                        log.info("📩 [Main Bot] Получено сообщение от user_id=$userId: $text")

                        if (!attachmentProcessed && !text.isNullOrBlank()) {
                            handleTextMessage(userId, chatId, text)
                        }
                    }

                    "message_callback" -> {
                        val callbackData = update["callback"] as? Map<*, *> ?: return@forEach
                        val userData = callbackData["user"] as? Map<*, *> ?: return@forEach
                        val userId = (userData["user_id"] as? Number)?.toString() ?: return@forEach
                        val payload = callbackData["payload"] as? String ?: return@forEach

                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val recipient = message["recipient"] as? Map<*, *> ?: return@forEach
                        val chatId = (recipient["chat_id"] as? Number)?.toString() ?: return@forEach

                        log.info("🔘 [Main Bot] Callback от user_id=$userId, payload=$payload")
                        handleAuthCallback(userId, chatId, payload)
                    }

                    else -> {
                        log.debug("⚠️ [Main Bot] Неизвестный тип: $updateType")
                    }
                }
            }

        } catch (_: ResourceAccessException) {
            // таймаут — нормально
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка в Long Polling", e)
        }
    }

    private fun parseContactFromVcf(vcfInfo: String): ContactInfo? {
        return try {
            val vcard = Ezvcard.parse(vcfInfo).first()
            val phone = vcard.telephoneNumbers.firstOrNull()?.text
            val name = vcard.formattedName?.value ?: "Пользователь"
            if (phone != null) ContactInfo(phone = phone, name = name) else null
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка парсинга VCF: ${e.message}")
            null
        }
    }

    data class ContactInfo(val phone: String, val name: String)

    /**
     * Проверяет, является ли пользователь сотрудником
     */
    private fun isEmployee(chatId: String): Boolean {
        val user = maxUserAgnService.findByChatId(chatId) ?: return false
        val cleanPhone = PhoneUtils.getPhoneTail(user.phone ?: "")
        return phonebookRepository.findByPhoneTail(cleanPhone).isNotEmpty()
    }

    private fun sendMainMenu(chatId: String) {
        val isRegistered = maxUserAgnService.isChatRegistered(chatId)
        val employee = isRegistered && isEmployee(chatId)

        val buttons = if (isRegistered) {
            val list = mutableListOf<List<Map<String, Any>>>()
            list.add(listOf(mapOf("type" to "callback", "text" to "📋 Мои данные", "payload" to "my_data")))

            if (employee) {
                list.add(listOf(mapOf("type" to "callback", "text" to "🔍 Поиск сотрудников", "payload" to "search")))
            }

            list.add(listOf(mapOf("type" to "callback", "text" to "🗑️ Отписаться", "payload" to "unsubscribe")))
            list.add(listOf(mapOf("type" to "callback", "text" to "ℹ️ Помощь", "payload" to "auth_help")))
            list
        } else {
            listOf(
                listOf(mapOf("type" to "callback", "text" to "📝 Регистрация", "payload" to "register")),
                listOf(mapOf("type" to "callback", "text" to "ℹ️ Помощь", "payload" to "auth_help"))
            )
        }

        val text = when {
            !isRegistered -> """
                👋 *Добро пожаловать!*
                
                Для начала работы необходимо зарегистрироваться.
                Нажмите *"Регистрация"* для начала.
            """.trimIndent()
            employee -> """
                👋 *Привет, сотрудник!*
                
                Вы уже зарегистрированы.
                Выберите действие в меню ниже.
            """.trimIndent()
            else -> """
                👋 *Привет!*
                
                Вы уже зарегистрированы.
                Выберите действие в меню ниже.
            """.trimIndent()
        }

        botClient.sendMessageWithKeyboard(chatId, text, buttons, "markdown")
    }

    private fun sendHelp(chatId: String) {
        val buttons = listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ В меню",
                    "payload" to "back_to_menu"
                )
            )
        )

        botClient.sendMessageWithKeyboard(
            chatId,
            """
            📖 *Помощь*
            
            🔐 *Как зарегистрироваться:*
            1. Нажмите *Регистрация* в главном меню
            2. Нажмите *Поделиться номером*
            3. Подтвердите отправку номера
            4. Дождитесь сообщения об успешной регистрации
            
            👤 *Кто может зарегистрироваться:*
            Клиенты магазина.
            Сотрудники организации.
            Номер телефона должен совпадать с номером, с которым вы зарегистрированы в MAX.
            
            🛒 *Как стать клиентом:*
            Обратитесь в информационную службу.
            
            📌 *Команды:*
            /start — главное меню
            /help — эта справка
            """.trimIndent(),
            buttons,
            "markdown"
        )
    }

    private fun handleTextMessage(userId: String, chatId: String, text: String) {
        val trimmedText = text.trim()

        // ✅ Если пользователь в состоянии поиска
        if (searchState[userId] == true) {
            searchState[userId] = false
            if (trimmedText.isNotEmpty()) {
                searchEmployees(chatId, trimmedText)
            } else {
                botClient.sendMessage(
                    chatId,
                    "❌ Поисковый запрос не может быть пустым.",
                    "markdown"
                )
            }
            return
        }

        when (trimmedText.lowercase()) {
            "/start" -> {
                log.info("📌 [Main Bot] Пользователь $userId вызвал /start")
                sendMainMenu(chatId)
            }

            "/help" -> {
                log.info("📌 [Main Bot] Пользователь $userId вызвал /help")
                sendHelp(chatId)
            }

            else -> {
                log.info("📌 [Main Bot] Неизвестная команда от $userId: $trimmedText, показываем меню")
                sendMainMenu(chatId)
            }
        }
    }

    private fun handlePhoneNumberReceived(userId: String, chatId: String, contact: ContactInfo) {
        if (maxUserAgnService.isChatRegistered(chatId)) {
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithKeyboard(chatId, "ℹ️ *Вы уже зарегистрированы!*", buttons, "markdown")
            return
        }

        val cleanNumber = contact.phone.replace(Regex("[^\\d+]"), "")
        val nameFromMax = contact.name
        val phoneTail = PhoneUtils.getPhoneTail(cleanNumber)

        log.info("📱 [Main Bot] ✅ ПОЛУЧЕН КОНТАКТ: userId=$userId, chatId=$chatId, phone=$cleanNumber, nameFromMax=$nameFromMax")

        // ✅ Сначала проверяем в phonebook (сотрудники)
        val employeeResults = phonebookRepository.findByPhoneTail(phoneTail)
            .ifEmpty { phonebookRepository.findByPhoneSot(cleanNumber) }
            .ifEmpty { phonebookRepository.findByPhoneInt(cleanNumber) }

        // ✅ Потом проверяем в agnphonenumberlist (клиенты)
        val clientResults = agnPhoneService.searchByPhone(cleanNumber)

        // ✅ Если не найден нигде — ошибка
        if (employeeResults.isEmpty() && clientResults.isEmpty()) {
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithKeyboard(
                chatId,
                """
            ❌ *Номер `$cleanNumber` не найден!*
            
            Пожалуйста, проверьте правильность номера или обратитесь к администратору.
            """.trimIndent(),
                buttons,
                "markdown"
            )
            return
        }

        // ✅ Определяем роль: сотрудник ИЛИ клиент
        val isEmployee = employeeResults.isNotEmpty()  // Сотрудник?
        val isClient = clientResults.isNotEmpty()      // Клиент?

        // ✅ Формируем текст роли
        val roleText = when {
            isEmployee && isClient -> "сотрудник + клиент"
            isEmployee -> "сотрудник"
            isClient -> "клиент"
            else -> "пользователь"
        }

        try {
            maxUserAgnService.addUserAgn(
                userId = userId,
                chatId = chatId,
                phone = cleanNumber,
                botType = "MAIN",
                userName = nameFromMax
            )

            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )

            botClient.sendMessageWithKeyboard(
                chatId,
                """
            👋 *Привет, $nameFromMax!*
            
            ✅ Регистрация прошла успешно!
            
            📱 Ваш номер: `$cleanNumber`
            👤 Роль: $roleText
            
            🔐 Добро пожаловать!
            """.trimIndent(),
                buttons,
                "markdown"
            )

        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка регистрации", e)
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithKeyboard(chatId, "❌ Ошибка регистрации. Попробуйте позже.", buttons, "markdown")
        }
    }

    private fun handleAuthCallback(userId: String, chatId: String, payload: String) {
        log.info("🔘 [Main Bot] Callback: userId=$userId, chatId=$chatId, payload=$payload")

        when {
            payload == "register" -> {
                if (maxUserAgnService.isChatRegistered(chatId)) {
                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "◀️ В меню",
                                "payload" to "back_to_menu"
                            )
                        )
                    )
                    botClient.sendMessageWithKeyboard(chatId, "ℹ️ *Вы уже зарегистрированы!*", buttons, "markdown")
                    return
                }

                log.info("📝 [Main Bot] Пользователь $userId нажал 'Регистрация'")

                val buttons = listOf(
                    listOf(
                        mapOf(
                            "type" to "request_contact",
                            "text" to "📱 Поделиться номером"
                        )
                    ),
                    listOf(
                        mapOf(
                            "type" to "callback",
                            "text" to "◀️ Назад",
                            "payload" to "back_to_menu"
                        )
                    )
                )

                botClient.sendMessageWithKeyboard(
                    chatId,
                    """
                    📝 *Регистрация*
                    
                    Нажмите *"Поделиться номером"* для отправки контакта.
                    
                    ⚠️ *Важно:*
                    Вы должны быть клиентом магазина или сотрудником.
                    Номер в боте MAX должен совпадать с номером в системе.
                    Если номер не найден, регистрация будет отклонена.
                    Для получения карты обратитесь в информационную службу.
                    """.trimIndent(),
                    buttons,
                    "markdown"
                )
            }

            payload == "search" -> {
                if (!isEmployee(chatId)) {
                    botClient.sendMessage(chatId, "❌ Поиск сотрудников доступен только сотрудникам", "markdown")
                    return
                }
                searchState[userId] = true
                botClient.sendMessage(chatId, "🔍 Введите фамилию, имя или должность:", "markdown")
            }

            payload == "my_data" -> {
                val userAgn = maxUserAgnService.findByChatId(chatId)
                if (userAgn != null) {
                    val employee = isEmployee(chatId)
                    val roleText = if (employee) "Сотрудник" else "Клиент"
                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "◀️ В меню",
                                "payload" to "back_to_menu"
                            )
                        )
                    )
                    botClient.sendMessageWithKeyboard(
                        chatId,
                        """
                        📋 *Ваши данные:*
                        
                        📱 Номер: `${userAgn.phone}`
                        👤 Имя: ${userAgn.userName ?: "не указано"}
                        🏷️ Роль: $roleText
                        📅 Зарегистрирован: ${userAgn.createdAt}
                        """.trimIndent(),
                        buttons,
                        "markdown"
                    )
                } else {
                    sendMainMenu(chatId)
                }
            }

            payload == "unsubscribe" -> {
                val buttons = listOf(
                    listOf(
                        mapOf(
                            "type" to "callback",
                            "text" to "✅ Да, отписаться",
                            "payload" to "confirm_unsubscribe"
                        )
                    ),
                    listOf(
                        mapOf(
                            "type" to "callback",
                            "text" to "❌ Нет, остаться",
                            "payload" to "back_to_menu"
                        )
                    )
                )

                botClient.sendMessageWithKeyboard(
                    chatId,
                    """
                    🗑️ *Отписка*
                    
                    Вы уверены, что хотите отписаться?
                    
                    После отписки вы больше не будете получать:
                    • Коды для подтверждения на кассе
                    • Уведомления о заказах
                    • Другие сообщения от бота
                    
                    Вы всегда сможете зарегистрироваться снова.
                    """.trimIndent(),
                    buttons,
                    "markdown"
                )
            }

            payload == "confirm_unsubscribe" -> {
                try {
                    maxUserAgnService.deleteByChatId(chatId)

                    log.info("🗑️ [Main Bot] Пользователь $chatId отписался")

                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "📝 Зарегистрироваться снова",
                                "payload" to "register"
                            )
                        )
                    )

                    botClient.sendMessageWithKeyboard(
                        chatId,
                        """
                        ✅ *Вы отписались!*
                        
                        Вы больше не будете получать уведомления.
                        
                        Если захотите вернуться, нажмите *"Зарегистрироваться снова"*.
                        """.trimIndent(),
                        buttons,
                        "markdown"
                    )

                    sendMainMenu(chatId)

                } catch (e: Exception) {
                    log.error("❌ [Main Bot] Ошибка отписки", e)
                    botClient.sendMessage(chatId, "❌ Ошибка при отписке. Попробуйте позже.", "markdown")
                    sendMainMenu(chatId)
                }
            }

            payload.startsWith("call_mobile_") -> {
                val parts = payload.removePrefix("call_mobile_").split("_")
                val internalNumber = parts.getOrNull(0) ?: ""
                val callerNumber = parts.getOrNull(1) ?: ""

                log.info("📱 [Main Bot] Обработка перезвона: сотрудник=$internalNumber, звонящий=$callerNumber")
                handleCallMobile(chatId, internalNumber, callerNumber)
            }

            payload == "auth_help" -> {
                sendHelp(chatId)
            }

            payload == "back_to_menu" -> {
                sendMainMenu(chatId)
            }

            else -> {
                log.warn("⚠️ [Main Bot] Неизвестный payload: $payload")
                sendMainMenu(chatId)
            }
        }
    }

    /**
     * Обработка перезвона на сотовый сотрудника
     */
    private fun handleCallMobile(chatId: String, internalNumber: String, callerNumber: String) {
        try {
            val callerInfo = callNotificationService.findCallerInfo(callerNumber)
            val callerName = callerInfo?.let {
                listOfNotNull(it.fname, it.nname).joinToString(" ")
            } ?: callerNumber

            val employeeInfo = callNotificationService.findCallerInfo(internalNumber)
            val employeePhoneSot = employeeInfo?.phoneSot?.takeIf { it.isNotBlank() }

            if (employeePhoneSot == null) {
                botClient.sendMessage(
                    chatId,
                    "❌ У вас не указан сотовый номер в телефонной книге.",
                    "markdown"
                )
                return
            }

            val phoneSot = PhoneUtils.phone8(employeePhoneSot)
            val employeeName = listOfNotNull(employeeInfo?.fname, employeeInfo?.nname).joinToString(" ")

            val dialNumber = when {
                callerNumber.matches(Regex("^\\d{3,4}$")) -> callerNumber
                else -> callerNumber
            }

            log.info("📱 Перезвон: звоним на $dialNumber, соединяем с сотовым $phoneSot, CallerID=$employeeName")

            botClient.sendMessage(
                chatId,
                """
                📱 *Идёт перезвон на сотовый!*
                
                ⏳ Сейчас мы дозвонимся до $callerName и позвоним на ваш сотовый $phoneSot.
                """.trimIndent(),
                "markdown"
            )

            asteriskService.originateCall(
                dialNumber,
                "800$phoneSot",
                employeeName
            )

        } catch (e: Exception) {
            log.error("❌ Ошибка перезвона", e)
            botClient.sendMessage(
                chatId,
                "❌ Ошибка при выполнении перезвона. Попробуйте позже.",
                "markdown"
            )
        }
    }

    /**
     * Поиск сотрудников по фамилии, имени, должности или отделу
     */
    private fun searchEmployees(chatId: String, query: String) {
        val searchLower = query.lowercase()

        val allEmployees = phonebookRepository.findAll()
        val results = allEmployees.filter { employee ->
            val fullName = listOfNotNull(employee.fname, employee.nname).joinToString(" ")

            fullName.lowercase().contains(searchLower) ||
                    employee.dolgnost?.lowercase()?.contains(searchLower) == true ||
                    employee.otdel?.lowercase()?.contains(searchLower) == true ||
                    employee.fname?.lowercase()?.contains(searchLower) == true ||
                    employee.nname?.lowercase()?.contains(searchLower) == true
        }.take(10)

        if (results.isEmpty()) {
            botClient.sendMessage(
                chatId,
                "🔍 По запросу `$query` ничего не найдено.",
                "markdown"
            )
            return
        }

        val message = buildSearchResult(results, query)
        botClient.sendMessage(chatId, message, "markdown")
    }

    /**
     * Формирование результата поиска
     */
    private fun buildSearchResult(results: List<Phonebook>, query: String): String {
        val lines = mutableListOf<String>()
        lines.add("🔍 *Результаты поиска*")
        lines.add("Запрос: `$query`")
        lines.add("Найдено: ${results.size}")
        lines.add("")

        results.forEachIndexed { index, employee ->
            val fullName = listOfNotNull(employee.fname, employee.nname).joinToString(" ")
            lines.add("${index + 1}. *$fullName*")
            employee.dolgnost?.let { lines.add("   📋 $it") }
            employee.otdel?.let { lines.add("   🏢 $it") }
            employee.phoneInt?.let { lines.add("   📞 $it") }
            employee.phoneSot?.let {
                lines.add("   📱 ${callNotificationService.formatPhoneNumber(it)}")
            }
            employee.email?.let { lines.add("   📧 $it") }
            lines.add("")
        }

        return lines.joinToString("\n")
    }

}