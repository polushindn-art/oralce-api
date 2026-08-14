package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.config.MaxApiProperties
import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.agnphonenumber.AgnPhoneService
import com.example.oracleapi.service.ats.AsteriskService
import com.example.oracleapi.service.ats.CallNotificationService
import com.example.oracleapi.service.barcode.BarcodeService
import com.example.oracleapi.service.max.AvatarService
import com.example.oracleapi.service.maxUserAgn.MaxUserAgnService
import com.example.oracleapi.service.stock.StockService
import com.example.oracleapi.service.website.WebSiteService
import com.example.oracleapi.util.BotButtons
import com.example.oracleapi.util.PhoneUtils
import ezvcard.Ezvcard
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

@Service
class MaxMainPollingService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties,
    private val botClient: MaxBotMainClient,
    private val agnPhoneService: AgnPhoneService,
    private val maxUserAgnService: MaxUserAgnService,
    private val barcodeService: BarcodeService,
    private val phonebookRepository: PhonebookRepository,
    private val asteriskService: AsteriskService,
    private val callNotificationService: CallNotificationService,
    private val avatarService: AvatarService,
    private val webSiteService: WebSiteService,
    private val stockService: StockService
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private var marker: Long? = null
    private val searchState = ConcurrentHashMap<String, Long>()

    data class CacheEntry(val url: String, val timestamp: Long)

    // ✅ Кэш для аватаров (chatId -> avatarUrl)
    private val avatarCache = ConcurrentHashMap<String, CacheEntry>()

    @Scheduled(fixedDelay = 3000, initialDelay = 5000)
    fun pollUpdates() {
        cleanupCaches()
        try {
            val uriBuilder = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/updates")
                .queryParam("limit", 5)
                .queryParam("timeout", 20)

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

                log.info("📦 [Main Bot] ВСЕ update: $update")  // ← Логируем всё!
                log.info("📦 [Main Bot] update_type: ${update["update_type"]}")

                when (val updateType = update["update_type"] as? String) {
                    "bot_started" -> {
                        log.info("🚀 [Main Bot] ========== НАЧАЛО bot_started ==========")

                        val user = update["user"] as? Map<*, *>
                        log.info("📋 [Main Bot] user: $user")

                        val userId = (user?.get("user_id") as? Number)?.toString()
                            ?: (update["user_id"] as? Number)?.toString()
                            ?: run {
                                log.warn("⚠️ [Main Bot] Не найден user_id в update: $update")
                                return@forEach
                            }

                        val chatId = (update["chat_id"] as? Number)?.toString()
                            ?: run {
                                log.warn("⚠️ [Main Bot] Не найден chat_id в update: $update")
                                return@forEach
                            }

                        // ✅ Логируем ВСЕ поля user
                        log.info("📋 [Main Bot] user_id: $userId")
                        log.info("📋 [Main Bot] chat_id: $chatId")
                        log.info("📋 [Main Bot] user.first_name: ${user?.get("first_name")}")
                        log.info("📋 [Main Bot] user.last_name: ${user?.get("last_name")}")
                        log.info("📋 [Main Bot] user.name: ${user?.get("name")}")
                        log.info("📋 [Main Bot] user.is_bot: ${user?.get("is_bot")}")
                        log.info("📋 [Main Bot] user.last_activity_time: ${user?.get("last_activity_time")}")

                        // ✅ ОСОБО ВНИМАТЕЛЬНО логируем avatar_url и full_avatar_url
                        val avatarUrl = user?.get("avatar_url") as? String
                        val fullAvatarUrl = user?.get("full_avatar_url") as? String

                        log.info("📸 [Main Bot] avatar_url: $avatarUrl")
                        log.info("📸 [Main Bot] full_avatar_url: $fullAvatarUrl")

                        // ✅ Проверяем все ключи в user, чтобы увидеть, что вообще приходит
                        if (user != null) {
                            log.info("📋 [Main Bot] Все ключи в user: ${user.keys.joinToString()}")
                        }

                        // ✅ Сохраняем аватар в кэш (если есть)
                        val avatarToUse = avatarCache.remove(chatId)
                        if (avatarToUse != null) {
                            avatarCache[chatId] = avatarToUse // <-- Ошибка здесь
                            log.info("📸 [Main Bot] ✅ Сохранён аватар для chatId=$chatId: $avatarToUse")
                        } else {
                            log.warn("⚠️ [Main Bot] ❌ Аватар НЕ НАЙДЕН в bot_started для chatId=$chatId")
                        }

                        log.info("🚀 [Main Bot] Бот запущен: userId=$userId, chatId=$chatId")

                        try {
                            maxUserAgnService.activateByChatId(chatId)
                            log.info("✅ [Main Bot] Пользователь $chatId активирован")
                        } catch (e: Exception) {
                            log.error("❌ [Main Bot] Ошибка активации пользователя $chatId", e)
                        }

                        sendMainMenu(chatId)
                        log.info("🚀 [Main Bot] ========== КОНЕЦ bot_started ==========")
                    }

                    "bot_stopped" -> {
                        val user = update["user"] as? Map<*, *>
                        val userId = (user?.get("user_id") as? Number)?.toString()
                            ?: (update["user_id"] as? Number)?.toString()
                            ?: return@forEach

                        val chatId = (update["chat_id"] as? Number)?.toString()
                            ?: return@forEach

                        log.info("🛑 [Main Bot] ПОЛУЧЕН bot_stopped! userId=$userId, chatId=$chatId")

                        // ✅ Проверяем, есть ли пользователь в БД
                        val userAgn = maxUserAgnService.findByChatId(chatId)
                        log.info("📋 [Main Bot] Найден пользователь: ${userAgn != null}")

                        if (userAgn != null) {
                            log.info("✅ [Main Bot] Пользователь найден: ${userAgn.userName}")
                            maxUserAgnService.deactivateByChatId(chatId)
                            log.info("✅ [Main Bot] Пользователь деактивирован")
                        } else {
                            log.warn("⚠️ [Main Bot] Пользователь с chatId=$chatId НЕ НАЙДЕН в БД")
                        }
                    }

                    "message_created" -> {
                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val sender = message["sender"] as? Map<*, *>
                        val recipient = message["recipient"] as? Map<*, *>
                        val bodyMap = message["body"] as? Map<*, *>

                        val userId = (sender?.get("user_id") as? Number)?.toString() ?: return@forEach
                        val chatId = (recipient?.get("chat_id") as? Number)?.toString() ?: return@forEach
                        val text = bodyMap?.get("text") as? String

                        var attachmentProcessed = false

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
                                    attachmentProcessed = true
                                    val payload = attachmentMap["payload"] as? Map<*, *>
                                    val photoUrl = payload?.get("url") as? String
                                    val photoToken = payload?.get("token") as? String

                                    log.info("📸 [Main Bot] Получено ФОТО от user_id=$userId, chatId=$chatId")
                                    log.info("📸 [Main Bot] URL: $photoUrl")
                                    log.info("📸 [Main Bot] TOKEN: $photoToken")

                                    if (photoUrl != null) {
                                        val authToken = "Bearer ${properties.botMainToken}"

                                        //val barcode = barcodeService.decodeBarcodeFromUrlWithAuth(photoUrl, authToken)
                                        val barcode = runBlocking {
                                            barcodeService.decodeBarcodeFromUrlWithAuth(photoUrl, authToken)
                                        }

                                        if (barcode != null) {
                                            log.info("📱 [Main Bot] Распознан код: $barcode")

                                            val nomenText =  stockService.getFullStockMessageByBarcode(barcode)

                                            botClient.sendMessageWithInlineKeyboard(
                                                chatId,
                                                nomenText,
                                                BotButtons.menuButton(),
                                                "markdown"
                                            )
                                        } else {
                                            botClient.sendMessageWithInlineKeyboard(
                                                chatId,
                                                "❌ Не удалось распознать код на фото",
                                                BotButtons.menuButton(),
                                                "markdown"
                                            )
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

        } catch (e: HttpClientErrorException.TooManyRequests) {
            log.warn("⚠️ 429 Too Many Requests, ждём 5 секунд...")
            Thread.sleep(5000)
        } catch (e: HttpClientErrorException.Unauthorized) {
            log.error("❌ 401 Unauthorized! Проверьте токен botMainToken")
            Thread.sleep(30000)
        } catch (e: ResourceAccessException) {
            log.trace("⏳ Long Polling таймаут")
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка в Long Polling", e)
            Thread.sleep(2000)
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

    private fun isEmployee(chatId: String): Boolean {
        val user = maxUserAgnService.findByChatId(chatId) ?: return false
        val cleanPhone = PhoneUtils.getPhoneTail(user.phone ?: "")
        return phonebookRepository.findByPhoneTail(cleanPhone).isNotEmpty()
    }

    /**
     * ✅ Главное меню с inline-кнопками
     */
    private fun sendMainMenu(chatId: String) {
        log.info("📋 [Main Bot] Отправка главного меню в chatId=$chatId")

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

        try {
            botClient.sendMessageWithInlineKeyboard(chatId, text, buttons, "markdown")
            log.info("✅ [Main Bot] Меню отправлено в chatId=$chatId")
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка отправки меню в chatId=$chatId", e)
        }
    }

    /**
     * ✅ Помощь с кнопкой "В меню"
     */
    private fun sendHelp(chatId: String) {
        val text = """
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
        """.trimIndent()

        botClient.sendMessageWithInlineKeyboard(chatId, text, BotButtons.menuButton(), "markdown")
    }

    private fun handleTextMessage(userId: String, chatId: String, text: String) {
        val trimmedText = text.trim()

        if (searchState.remove(userId) != null) {
            if (trimmedText.isNotEmpty()) {
                searchEmployees(chatId, trimmedText)
            } else {
                botClient.sendMessage(chatId, "❌ Поисковый запрос не может быть пустым.", "markdown")
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

    /**
     * ✅ Регистрация с кнопкой "В меню"
     */
    private fun handlePhoneNumberReceived(userId: String, chatId: String, contact: ContactInfo) {
        if (maxUserAgnService.isChatRegistered(chatId)) {
            botClient.sendMessageWithInlineKeyboard(
                chatId,
                "ℹ️ *Вы уже зарегистрированы!*",
                BotButtons.menuButton(),
                "markdown"
            )
            return
        }

        val cleanNumber = contact.phone.replace(Regex("[^\\d+]"), "")
        val nameFromMax = contact.name
        val phoneTail = PhoneUtils.getPhoneTail(cleanNumber)

        log.info("📱 [Main Bot] ✅ ПОЛУЧЕН КОНТАКТ: userId=$userId, chatId=$chatId, phone=$cleanNumber, nameFromMax=$nameFromMax")

        val employeeResults = phonebookRepository.findByPhoneTail(phoneTail)
            .ifEmpty { phonebookRepository.findByPhoneSot(cleanNumber) }
            .ifEmpty { phonebookRepository.findByPhoneInt(cleanNumber) }

        val clientResults = agnPhoneService.searchByPhone(cleanNumber)

        if (employeeResults.isEmpty() && clientResults.isEmpty()) {
            botClient.sendMessageWithInlineKeyboard(
                chatId,
                """
            ❌ *Номер `$cleanNumber` не найден!*
            
            Пожалуйста, проверьте правильность номера или обратитесь к администратору.
            """.trimIndent(),
                BotButtons.menuButton(),
                "markdown"
            )
            return
        }

        val isEmployee = employeeResults.isNotEmpty()
        val isClient = clientResults.isNotEmpty()

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

            // ✅ Если есть аватар в кэше — скачиваем
            val avatarEntry = avatarCache.remove(chatId) // Теперь это CacheEntry, а не String
            if (avatarEntry != null) {
                try {
                    // Передаем avatarEntry.url в метод сохранения
                    avatarService.downloadAndSaveAvatar(chatId, avatarEntry.url)
                    log.info("✅ [Main Bot] Аватар скачан для нового пользователя $chatId")
                } catch (e: Exception) {
                    log.warn("⚠️ [Main Bot] Не удалось скачать аватар для $chatId", e)
                }
            }

            botClient.sendMessageWithInlineKeyboard(
                chatId,
                """
            👋 *Привет, $nameFromMax!*
            
            ✅ Регистрация прошла успешно!
            
            📱 Ваш номер: `$cleanNumber`
            👤 Роль: $roleText
            
            🔐 Добро пожаловать!
            """.trimIndent(),
                BotButtons.menuButton(),
                "markdown"
            )

        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка регистрации", e)
            botClient.sendMessageWithInlineKeyboard(
                chatId,
                "❌ Ошибка регистрации. Попробуйте позже.",
                BotButtons.menuButton(),
                "markdown"
            )
        }
    }

    private fun handleAuthCallback(userId: String, chatId: String, payload: String) {
        log.info("🔘 [Main Bot] Callback: userId=$userId, chatId=$chatId, payload=$payload")

        when {
            payload == "register" -> {
                if (maxUserAgnService.isChatRegistered(chatId)) {
                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        "ℹ️ *Вы уже зарегистрированы!*",
                        BotButtons.menuButton(),
                        "markdown"
                    )
                    return
                }

                log.info("📝 [Main Bot] Пользователь $userId нажал 'Регистрация'")

                botClient.sendMessageWithInlineKeyboard(
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
                    BotButtons.registerButtons(),
                    "markdown"
                )
            }

            payload == "search" -> {
                if (!isEmployee(chatId)) {
                    botClient.sendMessage(chatId, "❌ Поиск сотрудников доступен только сотрудникам", "markdown")
                    return
                }
                searchState[userId] = System.currentTimeMillis()
                botClient.sendMessage(chatId, "🔍 Введите фамилию, имя или должность:", "markdown")
            }

            payload == "my_data" -> {
                val userAgn = maxUserAgnService.findByChatId(chatId)
                if (userAgn != null) {
                    val employee = isEmployee(chatId)
                    val roleText = if (employee) "Сотрудник" else "Клиент"

                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    val formattedDate = userAgn.createdAt?.format(formatter) ?: "не указано"

                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        """
                        📋 *Ваши данные:*
                        
                        📱 Номер: `${userAgn.phone}`
                        👤 Имя: ${userAgn.userName ?: "не указано"}
                        🏷️ Роль: $roleText
                        📅 Зарегистрирован: $formattedDate
                        """.trimIndent(),
                        BotButtons.menuButton(),
                        "markdown"
                    )
                } else {
                    sendMainMenu(chatId)
                }
            }

            payload == "unsubscribe" -> {

                botClient.sendMessageWithInlineKeyboard(
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
                    BotButtons.unsubscribeButtons(),
                    "markdown"
                )
            }

            payload == "confirm_unsubscribe" -> {
                try {
                    maxUserAgnService.deleteByChatId(chatId)
                    log.info("🗑️ [Main Bot] Пользователь $chatId отписался")


                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        """
                        ✅ *Вы отписались!*
                        
                        Вы больше не будете получать уведомления.
                        
                        Если захотите вернуться, нажмите *"Зарегистрироваться снова"*.
                        """.trimIndent(),
                        BotButtons.registerAgainButton(),
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

    private fun handleCallMobile(chatId: String, internalNumber: String, callerNumber: String) {
        try {
            val callerInfo = callNotificationService.findCallerInfo(callerNumber)
            val callerName = callerInfo?.let {
                listOfNotNull(it.fname, it.nname).joinToString(" ")
            } ?: callerNumber

            val employeeInfo = callNotificationService.findCallerInfo(internalNumber)
            val employeePhoneSot = employeeInfo?.phoneSot?.takeIf { it.isNotBlank() }

            if (employeePhoneSot == null) {
                botClient.sendMessage(chatId, "❌ У вас не указан сотовый номер в телефонной книге.", "markdown")
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
            botClient.sendMessage(chatId, "❌ Ошибка при выполнении перезвона. Попробуйте позже.", "markdown")
        }
    }

    /**
     * Поиск сотрудников
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
            // ✅ Если ничего не найдено — показываем кнопку "Новый поиск"
            val buttons = listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "🔍 Новый поиск",
                        "payload" to "search"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "◀️ В меню",
                        "payload" to "back_to_menu"
                    )
                )
            )
            botClient.sendMessageWithInlineKeyboard(
                chatId,
                "🔍 По запросу `$query` ничего не найдено.",
                buttons,
                "markdown"
            )
            return
        }

        val message = buildSearchResult(results, query)

        // ✅ Добавляем кнопки "Новый поиск" и "В меню"
        val buttons = listOf(
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "🔍 Новый поиск",
                    "payload" to "search"
                )
            ),
            listOf(
                mapOf(
                    "type" to "callback",
                    "text" to "◀️ В меню",
                    "payload" to "back_to_menu"
                )
            )
        )

        botClient.sendMessageWithInlineKeyboard(chatId, message, buttons, "markdown")
    }

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

    private fun cleanupCaches() {
        val oneHourAgo = System.currentTimeMillis() - 3600_000
        searchState.entries.removeIf { it.value < oneHourAgo }
        avatarCache.entries.removeIf { it.value.timestamp < oneHourAgo }
    }

}