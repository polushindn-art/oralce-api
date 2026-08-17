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
import com.example.oracleapi.util.BotButtons
import com.example.oracleapi.util.PhoneUtils
import ezvcard.Ezvcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
import java.util.concurrent.atomic.AtomicBoolean

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
    private val stockService: StockService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private var marker: Long? = null
    private val markerLock = Any()
    private val isInitialized = AtomicBoolean(false)

    private val searchState = ConcurrentHashMap<String, Long>()
    private val barcodeManualState = ConcurrentHashMap<String, Long>()

    data class CacheEntry(val url: String, val timestamp: Long)
    private val avatarCache = ConcurrentHashMap<String, CacheEntry>()
    private val employeeCache = ConcurrentHashMap<String, Boolean>()

    // ✅ Защита от дублирования callback'ов
    private val processedCallbacks = ConcurrentHashMap<String, Long>()

    // ✅ Ограниченный пул корутин
    private val botScope = CoroutineScope(Dispatchers.IO.limitedParallelism(10) + SupervisorJob())

    @Scheduled(fixedDelay = 2000, initialDelay = 1000)
    fun pollUpdates() {
        cleanupCaches()
        try {
            val currentMarker: Long?
            synchronized(markerLock) {
                currentMarker = marker
            }

            val uriBuilder = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/updates")
                .queryParam("limit", 20)

            if (currentMarker != null) {
                uriBuilder.queryParam("marker", currentMarker)
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
            val updates = body?.get("updates") as? List<*>
            val newMarker = (body?.get("marker") as? Number)?.toLong()

            // ✅ Безопасная инициализация
            if (!isInitialized.get()) {
                if (newMarker != null) {
                    synchronized(markerLock) {
                        marker = newMarker
                    }
                }
                isInitialized.set(true)
                log.info("✅ [Main Bot] Инициализация завершена, маркер: $marker")
                return
            }

            if (newMarker != null && newMarker != currentMarker) {
                synchronized(markerLock) {
                    marker = newMarker
                }
                log.info("🤖 [Main Bot] Обновлён маркер: $newMarker")
            }

            if (updates.isNullOrEmpty()) return

            updates.forEach { updateRaw ->
                botScope.launch {
                    try {
                        val update = updateRaw as? Map<*, *> ?: return@launch
                        processUpdate(update)
                    } catch (e: Exception) {
                        log.error("❌ Ошибка обработки апдейта", e)
                    }
                }
            }
        } catch (e: HttpClientErrorException.Unauthorized) {
            log.error("❌ 401 Unauthorized! Проверьте токен")
        } catch (e: ResourceAccessException) {
            // Таймаут — это нормально для Long Polling
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка в pollUpdates", e)
        }
    }

    private fun processUpdate(update: Map<*, *>) {
        when (update["update_type"] as? String) {
            "bot_started" -> handleBotStarted(update)
            "bot_stopped" -> handleBotStopped(update)
            "message_created" -> handleMessageCreated(update)
            "message_callback" -> handleMessageCallback(update)
        }
    }

    // ==================== ОБРАБОТЧИКИ СОБЫТИЙ ====================

    private fun handleBotStarted(update: Map<*, *>) {
        val user = update["user"] as? Map<*, *>
        val chatId = (update["chat_id"] as? Number)?.toString()
            ?: (user?.get("chat_id") as? Number)?.toString()
            ?: return

        val avatarUrl = user?.get("avatar_url") as? String
        if (avatarUrl != null) {
            avatarCache[chatId] = CacheEntry(avatarUrl, System.currentTimeMillis())
        }

        try {
            maxUserAgnService.activateByChatId(chatId)
        } catch (e: Exception) {
            log.error("❌ Ошибка активации пользователя $chatId", e)
        }
        sendMainMenu(chatId)
    }

    private fun handleBotStopped(update: Map<*, *>) {
        val chatId = (update["chat_id"] as? Number)?.toString() ?: return
        try {
            maxUserAgnService.deactivateByChatId(chatId)
        } catch (e: Exception) {
            log.error("❌ Ошибка деактивации пользователя $chatId", e)
        }
    }

    private fun handleMessageCreated(update: Map<*, *>) {
        val message = update["message"] as? Map<*, *> ?: return
        val sender = message["sender"] as? Map<*, *>
        val recipient = message["recipient"] as? Map<*, *>
        val bodyMap = message["body"] as? Map<*, *>

        val userId = (sender?.get("user_id") as? Number)?.toString() ?: return
        val chatId = (recipient?.get("chat_id") as? Number)?.toString() ?: return
        val text = bodyMap?.get("text") as? String

        val attachments = bodyMap?.get("attachments") as? List<*>

        attachments?.forEach { attachment ->
            val attachmentMap = attachment as? Map<*, *>
            when (attachmentMap?.get("type")) {
                "contact" -> {
                    val payload = attachmentMap["payload"] as? Map<*, *>
                    val vcfInfo = payload?.get("vcf_info") as? String
                    if (vcfInfo != null) {
                        parseContactFromVcf(vcfInfo)?.let {
                            handlePhoneNumberReceived(userId, chatId, it)
                        }
                    }
                    return  // Контакт — отдельное действие
                }
                "image" -> {
                    val payload = attachmentMap["payload"] as? Map<*, *>
                    val photoUrl = payload?.get("url") as? String
                    if (photoUrl != null) {
                        botScope.launch {
                            try {
                                val authToken = "Bearer ${properties.botMainToken}"
                                val barcode = barcodeService.decodeBarcodeFromUrlWithAuth(photoUrl, authToken)
                                if (barcode != null) {
                                    botClient.sendMessageWithInlineKeyboard(
                                        chatId,
                                        stockService.getFullStockMessageByBarcode(barcode),
                                        BotButtons.menuButton(),
                                        "markdown"
                                    )
                                } else {
                                    barcodeManualState[userId] = System.currentTimeMillis()
                                    botClient.sendMessageWithInlineKeyboard(
                                        chatId,
                                        "❌ Не распознано. Введите цифры вручную:",
                                        BotButtons.menuButton(),
                                        "markdown"
                                    )
                                }
                            } catch (e: Exception) {
                                log.error("❌ Ошибка обработки фото", e)
                            }
                        }
                    }
                }
            }
        }

        // ✅ Текст обрабатывается независимо от фото
        if (!text.isNullOrBlank()) {
            handleTextMessage(userId, chatId, text)
        }
    }

    // ✅ Защита от дублирования callback'ов (10 секунд)
    private fun handleMessageCallback(update: Map<*, *>) {
        val callbackData = update["callback"] as? Map<*, *> ?: return
        val userId = (callbackData["user"] as? Map<*, *>)?.get("user_id")?.toString() ?: return
        val payload = callbackData["payload"] as? String ?: return

        val message = update["message"] as? Map<*, *>
        val recipient = message?.get("recipient") as? Map<*, *>
        val chatId = recipient?.get("chat_id")?.toString() ?: return

        val callbackId = "$userId:$payload"
        val now = System.currentTimeMillis()
        val lastProcessed = processedCallbacks[callbackId]

        if (lastProcessed != null && now - lastProcessed < 10000) {
            log.info("⏭️ [Main Bot] Пропускаем дублирующий callback: $payload от userId=$userId")
            return
        }
        processedCallbacks[callbackId] = now

        handleAuthCallback(userId, chatId, payload)
    }

    // ==================== ОСТАЛЬНЫЕ МЕТОДЫ ====================

    private fun handleTextMessage(userId: String, chatId: String, text: String) {
        val trimmedText = text.trim()

        if (searchState.remove(userId) != null) {
            if (trimmedText.isNotEmpty()) searchEmployees(chatId, trimmedText)
            return
        }

        if (barcodeManualState.remove(userId) != null) {
            if (trimmedText.isNotEmpty()) {
                botClient.sendMessageWithInlineKeyboard(
                    chatId,
                    stockService.getFullStockMessageByBarcode(trimmedText),
                    BotButtons.menuButton(),
                    "markdown"
                )
            }
            return
        }

        when (trimmedText.lowercase()) {
            "/start" -> sendMainMenu(chatId)
            "/help" -> sendHelp(chatId)
            else -> sendMainMenu(chatId)
        }
    }

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

        try {
            maxUserAgnService.addUserAgn(
                userId = userId,
                chatId = chatId,
                phone = cleanNumber,
                botType = "MAIN",
                userName = contact.name
            )
            invalidateEmployeeCache(chatId)

            val avatarEntry = avatarCache.remove(chatId)
            if (avatarEntry != null) {
                try {
                    avatarService.downloadAndSaveAvatar(chatId, avatarEntry.url)
                } catch (e: Exception) {
                    log.warn("⚠️ Не удалось скачать аватар", e)
                }
            }

            botClient.sendMessageWithInlineKeyboard(
                chatId,
                "✅ *Регистрация прошла успешно!*\n\nДобро пожаловать!",
                BotButtons.menuButton(),
                "markdown"
            )
        } catch (e: Exception) {
            log.error("❌ Ошибка регистрации", e)
            botClient.sendMessage(chatId, "❌ Ошибка регистрации", "markdown")
        }
    }

    private fun handleAuthCallback(userId: String, chatId: String, payload: String) {
        when {
            payload == "register" -> {
                botClient.sendMessageWithInlineKeyboard(
                    chatId,
                    "📝 Нажмите *«Поделиться номером»* для регистрации.",
                    BotButtons.registerButtons(),
                    "markdown"
                )
            }
            payload == "product_search" -> {
                barcodeManualState[userId] = System.currentTimeMillis()
                botClient.sendMessageWithInlineKeyboard(
                    chatId,
                    "🔍 Отправьте **фото** штрих-кода или введите **цифры** вручную:",
                    BotButtons.menuButton(),
                    "markdown"
                )
            }
            payload == "search" -> {
                if (!isEmployee(chatId)) {
                    botClient.sendMessage(chatId, "❌ Доступно только сотрудникам", "markdown")
                    return
                }
                searchState[userId] = System.currentTimeMillis()
                botClient.sendMessage(chatId, "🔍 Введите запрос для поиска:", "markdown")
            }
            payload == "my_data" -> {
                val user = maxUserAgnService.findByChatId(chatId)
                if (user != null) {
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                    val formattedDate = user.createdAt?.format(formatter) ?: "не указано"
                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        """
                        📋 *Ваши данные:*
                        📱 Телефон: `${user.phone}`
                        👤 Имя: ${user.userName ?: "не указано"}
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
                    "🗑️ Вы уверены, что хотите отписаться?",
                    BotButtons.unsubscribeButtons(),
                    "markdown"
                )
            }
            payload == "confirm_unsubscribe" -> {
                try {
                    maxUserAgnService.deleteByChatId(chatId)
                    invalidateEmployeeCache(chatId)
                    processedCallbacks.entries.removeIf { it.key.startsWith("$userId:") }
                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        "✅ Вы отписались.",
                        BotButtons.registerAgainButton(),
                        "markdown"
                    )
                } catch (e: Exception) {
                    log.error("❌ Ошибка отписки", e)
                    botClient.sendMessage(chatId, "❌ Ошибка отписки", "markdown")
                }
            }
            payload.startsWith("call_mobile_") -> {
                val parts = payload.removePrefix("call_mobile_").split("_")
                handleCallMobile(
                    chatId,
                    parts.getOrNull(0) ?: "",
                    parts.getOrNull(1) ?: ""
                )
            }
            payload == "my_qr_card" -> {
                val userAgn = maxUserAgnService.findByChatId(chatId)

                if (userAgn == null) {
                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        "❌ Вы не зарегистрированы.",
                        BotButtons.menuButton(),
                        "markdown"
                    )
                    return
                }

                val cards = agnPhoneService.searchCardByPhone(userAgn.phone ?: "")

                if (cards.isEmpty()) {
                    botClient.sendMessageWithInlineKeyboard(
                        chatId,
                        """
                                ❌ *Дисконтная карта не найдена*
                                
                                Уважаемый покупатель!
                                
                                К сожалению, в нашей базе данных не найдена дисконтная карта,
                                привязанная к вашему номеру телефона: `${userAgn.phone}`
                                
                                📌 *Что делать:*
                                1. Обратитесь в *информационную службу* магазина
                                2. Предъявите номер телефона, указанный при регистрации
                                3. Сотрудник свяжет вашу карту с номером телефона
                                                                
                                После этого вы сможете пользоваться картой в боте.
                            """.trimIndent(),
                        BotButtons.menuButton(),
                        "markdown"
                    )
                    return
                }

                //Отправляем QR-коды для всех карт
                cards.forEachIndexed { index, card ->
                    val cardNumber = card.dscbarnumb!!
                    val cardIndex = index + 1 // 1, 2, 3, 4, 5...

                    val qrBytes = barcodeService.generateArsenalCard(cardNumber, cardIndex)

                    botClient.sendPhoto(
                        chatId = chatId,
                        photoBytes = qrBytes,
                        fileName = "qr_card_${cardNumber}.png",
                        caption = """
                        👤 Владелец: ${card.agnname ?: "не указан"}
                        """.trimIndent(),
                        buttons = BotButtons.menuButton()
                    )
                }
            }
            payload == "auth_help" -> sendHelp(chatId)
            payload == "back_to_menu" -> sendMainMenu(chatId)
            else -> {
                log.warn("⚠️ Неизвестный payload: $payload")
                sendMainMenu(chatId)
            }
        }
    }

    private fun handleCallMobile(chatId: String, internalNumber: String, callerNumber: String) {
        try {
            val callerInfo = callNotificationService.findCallerInfo(callerNumber)
            val employeeInfo = callNotificationService.findCallerInfo(internalNumber)
            val phoneSot = employeeInfo?.phoneSot?.takeIf { it.isNotBlank() }

            if (phoneSot == null) {
                botClient.sendMessage(chatId, "❌ Номер не найден.", "markdown")
                return
            }

            val employeeName = listOfNotNull(employeeInfo?.fname, employeeInfo?.nname).joinToString(" ")
            val formattedPhone = PhoneUtils.phone8(phoneSot)

            botClient.sendMessage(
                chatId,
                "📱 *Идёт перезвон на сотовый!*\n\n⏳ Дозваниваемся до $employeeName...",
                "markdown"
            )

            // ✅ Исправленный вызов
            asteriskService.originateCall(
                internalNumber = callerNumber,  // ← Внутренний номер (кому звоним)
                externalNumber = "800$formattedPhone"  // ← Внешний номер (отображается)
            )
        } catch (e: Exception) {
            log.error("❌ Ошибка перезвона", e)
            botClient.sendMessage(chatId, "❌ Ошибка при перезвоне", "markdown")
        }
    }

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

        val buttons = listOf(
            listOf(mapOf("type" to "callback", "text" to "🔍 Новый поиск", "payload" to "search")),
            listOf(mapOf("type" to "callback", "text" to "◀️ В меню", "payload" to "back_to_menu"))
        )

        if (results.isEmpty()) {
            botClient.sendMessageWithInlineKeyboard(
                chatId,
                "🔍 По запросу `$query` ничего не найдено.",
                buttons,
                "markdown"
            )
            return
        }

        botClient.sendMessageWithInlineKeyboard(
            chatId,
            buildSearchResult(results, query),
            buttons,
            "markdown"
        )
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

    private fun parseContactFromVcf(vcfInfo: String): ContactInfo? {
        return try {
            val vcard = Ezvcard.parse(vcfInfo).first()
            ContactInfo(
                phone = vcard.telephoneNumbers.firstOrNull()?.text ?: "",
                name = vcard.formattedName?.value ?: "Пользователь"
            )
        } catch (e: Exception) {
            log.error("❌ Ошибка парсинга VCF", e)
            null
        }
    }

    data class ContactInfo(val phone: String, val name: String)

    private fun isEmployee(chatId: String): Boolean {
        return employeeCache.computeIfAbsent(chatId) {
            val user = maxUserAgnService.findByChatId(chatId) ?: return@computeIfAbsent false
            val cleanPhone = PhoneUtils.getPhoneTail(user.phone ?: "")
            phonebookRepository.findByPhoneTail(cleanPhone).isNotEmpty()
        }
    }

    private fun invalidateEmployeeCache(chatId: String) {
        employeeCache.remove(chatId)
    }

    private fun sendMainMenu(chatId: String) {
        val isRegistered = maxUserAgnService.isChatRegistered(chatId)
        val employee = isRegistered && isEmployee(chatId)

        val buttons = mutableListOf<List<Map<String, Any>>>().apply {
            if (isRegistered) {
                add(listOf(mapOf("type" to "callback", "text" to "📋 Мои данные", "payload" to "my_data")))
                add(listOf(mapOf("type" to "callback", "text" to "🔍 Поиск товара", "payload" to "product_search")))
                add(listOf(mapOf("type" to "callback", "text" to "🪪 Моя дисконтная карта", "payload" to "my_qr_card")))
                if (employee) {
                    add(listOf(mapOf("type" to "callback", "text" to "🔍 Поиск сотрудников", "payload" to "search")))
                }
                add(listOf(mapOf("type" to "callback", "text" to "🗑️ Отписаться", "payload" to "unsubscribe")))
                add(listOf(mapOf("type" to "callback", "text" to "ℹ️ Помощь", "payload" to "auth_help")))
            } else {
                add(listOf(mapOf("type" to "callback", "text" to "📝 Регистрация", "payload" to "register")))
                add(listOf(mapOf("type" to "callback", "text" to "ℹ️ Помощь", "payload" to "auth_help")))
            }
        }

        val text = if (!isRegistered) {
            "👋 *Добро пожаловать!*\n\nДля начала работы необходимо зарегистрироваться.\nНажмите *«Регистрация»*."
        } else {
            "Выберите действие в меню ниже:"
        }

        botClient.sendMessageWithInlineKeyboard(chatId, text, buttons, "markdown")
    }

    private fun sendHelp(chatId: String) {
        val text = """
            📖 *Помощь*
            
            🔍 *Как искать товар:*
            1. Нажмите *«Поиск товара»*
            2. Отправьте фото штрих-кода
            
            🔐 *Как зарегистрироваться:*
            1. Нажмите *«Регистрация»*
            2. Нажмите *«Поделиться номером»*
            
            👤 *Кто может зарегистрироваться:*
            Клиенты и сотрудники магазина.
        """.trimIndent()

        botClient.sendMessageWithInlineKeyboard(chatId, text, BotButtons.menuButton(), "markdown")
    }

    private fun cleanupCaches() {
        val now = System.currentTimeMillis()
        searchState.entries.removeIf { now - it.value > 3600_000 }
        avatarCache.entries.removeIf { now - it.value.timestamp > 3600_000 }
        processedCallbacks.entries.removeIf { now - it.value > 30000 }
    }
}