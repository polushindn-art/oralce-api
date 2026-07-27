package com.example.oracleapi.service.max

import com.example.oracleapi.config.MaxApiProperties
import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.ats.AsteriskService
import com.example.oracleapi.service.ats.CallNotificationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class MaxPollingService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties,
    private val botClient: MaxBotClient,
    private val maxUserService: MaxUserService,
    private val asteriskService: AsteriskService,
    private val callNotificationService: CallNotificationService,
    private val phonebookRepository: PhonebookRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private var marker: Long? = null
    private val registrationState = mutableMapOf<String, Boolean>()

    @Scheduled(fixedDelay = 2000)
    fun pollUpdates() {
        try {
            val uriBuilder = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/updates")
                .queryParam("limit", 10)
                .queryParam("timeout", 30)

            if (marker != null) {
                uriBuilder.queryParam("marker", marker)
            }

            val url = uriBuilder.build().toUri()

            val headers = org.springframework.http.HttpHeaders().apply {
                set("Authorization", properties.botToken)
            }

            val response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                org.springframework.http.HttpEntity<Nothing>(headers),
                Map::class.java
            )

            val body = response.body
            val updates = body?.get("updates") as? List<*> ?: return
            val newMarker = (body["marker"] as? Number)?.toLong()

            if (newMarker != null && newMarker != marker) {
                marker = newMarker
                log.info("Получены новые обновления. Позиция в очереди MAX: $marker")
            }

            updates.forEach { updateRaw ->
                val update = updateRaw as? Map<*, *> ?: return@forEach
                log.info("📦 Получен update: $updateRaw")
                when (val updateType = update["update_type"] as? String) {
                    "bot_started" -> {
                        val userId = (update["user_id"] as? Number)?.toString() ?: return@forEach
                        val chatId = (update["chat_id"] as? Number)?.toString() ?: return@forEach
                        log.info("🚀 Бот запущен пользователем: userId=$userId, chatId=$chatId")
                        sendMainMenu(userId, chatId)
                    }

                    "message_created" -> {
                        val message = update["message"] as? Map<*, *> ?: return@forEach
                        val sender = message["sender"] as? Map<*, *>
                        val recipient = message["recipient"] as? Map<*, *>
                        val bodyMap = message["body"] as? Map<*, *>

                        val userId = (sender?.get("user_id") as? Number)?.toString() ?: return@forEach
                        val chatId = (recipient?.get("chat_id") as? Number)?.toString() ?: return@forEach
                        val text = bodyMap?.get("text") as? String

                        log.info("📩 Получено сообщение от user_id=$userId: $text")

                        if (text != null) {
                            handleMessage(userId, chatId, text)
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

                        log.info("🔘 Получен callback от user_id=$userId, chatId=$chatId, payload=$payload")
                        handleCallback(userId, chatId, payload)
                    }

                    else -> {
                        log.debug("⚠️ Неизвестный тип события: $updateType")
                    }
                }
            }

        } catch (_: ResourceAccessException) {
            // таймаут — нормально
        } catch (e: Exception) {
            log.error("❌ Ошибка в Long Polling", e)
        }
    }

    private fun sendMainMenu(userId: String, chatId: String) {
        val existingUser = maxUserService.findByUserId(userId)

        val buttons = if (existingUser != null) {
            listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "📋 Мой ID",
                        "payload" to "my_id"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "🔍 Поиск сотрудников",
                        "payload" to "search"
                    )
                )
            )
        } else {
            listOf(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "📝 Регистрация",
                        "payload" to "register"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "🔍 Поиск сотрудников",
                        "payload" to "search"
                    )
                ),
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "ℹ️ Помощь",
                        "payload" to "help"
                    )
                )
            )
        }

        val text = if (existingUser != null) {
            val userName = existingUser.userName ?: "Сотрудник"
            """
            👋 *Привет, $userName!*
            
            ✅ Вы уже зарегистрированы!
            📋 Ваш внутренний номер: *${existingUser.internalNumber}*
            
            📞 Для звонка на внутренний номер наберите 36-11-36 + номер.
            🔍 Для поиска сотрудников используйте /search фамилия
            """.trimIndent()
                } else {
                    """
            👋 *Добро пожаловать!*
            
            Я бот для уведомлений о входящих звонках.
            
            Нажмите кнопку *Регистрация*, чтобы привязать ваш внутренний номер.
            """.trimIndent()
        }

        botClient.sendMessageWithKeyboard(userId, text, buttons, "markdown")
    }

    private fun handleCallback(userId: String, chatId: String, payload: String?) {
        log.info("🔘 Обработка callback: userId=$userId, chatId=$chatId, payload=$payload")

        if (payload == null) {
            log.warn("⚠️ Payload is null")
            return
        }

        when {
            payload.startsWith("call_mobile_") -> {
                val parts = payload.removePrefix("call_mobile_").split("_")
                val internalNumber = parts.getOrNull(0) ?: ""      // внутренний номер сотрудника (кому звонят)
                val callerNumber = parts.getOrNull(1) ?: ""        // номер звонящего (кто звонит)

                log.info("📱 Обработка перезвона: сотрудник=$internalNumber, звонящий=$callerNumber")

                // 🔍 Ищем информацию о звонящем
                val callerInfo = callNotificationService.findCallerInfo(callerNumber)
                val callerName = callerInfo?.let {
                    listOfNotNull(it.fname, it.nname).joinToString(" ")
                } ?: callerNumber

                // 🔍 Ищем внутренний номер звонящего (если есть)
                val callerInternalNumber = callerInfo?.phoneInt?.takeIf { it.isNotBlank() }

                // 🔍 Ищем сотовый номер сотрудника (того, кому перезванивают)
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

                // 🔍 Ищем имя сотрудника (для CallerID)
                val employeeName = employeeInfo.let {
                    listOfNotNull(it.fname, it.nname).joinToString(" ")
                }

                // ✅ Определяем, на какой номер звонить (звонящему)
                val dialNumber = when {
                    callerNumber.matches(Regex("^\\d{3,4}$")) -> callerNumber
                    callerInternalNumber != null -> callerInternalNumber
                    else -> callerNumber
                }

                log.info("📱 Перезвон: звоним на $dialNumber, соединяем с сотовым $employeePhoneSot, CallerID=$employeeName")

                botClient.sendMessage(
                    chatId,
                    """
                    📱 *Идёт перезвон на сотовый!*
                    
                    ⏳ Сейчас мы дозвонимся до $callerName и позвоним на ваш сотовый $employeePhoneSot.
                    """.trimIndent(),
                    "markdown"
                )

                // ✅ Звоним на номер звонящего
                asteriskService.originateCall(
                    dialNumber,
                    "800$employeePhoneSot",
                    employeeName
                )
            }

            payload == "register" -> {
                registrationState[userId] = true
                botClient.sendMessage(
                    chatId,
                    "📝 Введите ваш внутренний номер телефона (только цифры).",
                    "markdown"
                )
            }

            payload == "my_id" -> {
                val user = maxUserService.findByUserId(userId)
                if (user != null) {
                    val userName = user.userName ?: "Сотрудник"
                    botClient.sendMessage(
                        chatId,
                        """
                    👤 *$userName*
                    📋 Ваш user_id: `$userId`
                    📋 chat_id: `$chatId`
                    📋 Внутренний номер: *${user.internalNumber}*
                    """.trimIndent(),
                        "markdown"
                    )
                } else {
                    botClient.sendMessage(
                        chatId,
                        "Вы ещё не зарегистрированы. Нажмите 'Регистрация'.",
                        "markdown"
                    )
                }
            }

            payload == "help" -> {
                botClient.sendMessage(
                    chatId,
                    """
                📖 *Помощь*
                /start — главное меню
                /id — показать ваши ID
                /register — зарегистрировать номер
                """.trimIndent(),
                    "markdown"
                )
            }

            payload == "search" -> {
                botClient.sendMessage(
                    chatId,
                    """
                        🔍 *Поиск сотрудников*
                
                        Введите фамилию, имя, должность или отдел.
                
                        Например:
                        `/search Иванов`
                        `/search Программист`
                        `/search ИТ`
                        """.trimIndent(),
                    "markdown"
                )
            }

            else -> {
                log.warn("⚠️ Неизвестный payload: $payload")
            }
        }
    }

    private fun handleMessage(userId: String, chatId: String, text: String) {
        val trimmedText = text.trim()

        // Проверяем, находится ли пользователь в процессе регистрации
        if (registrationState[userId] == true) {
            if (trimmedText.matches(Regex("^\\d+$"))) {
                val existingUser = maxUserService.findByInternalNumber(trimmedText)
                if (existingUser != null) {
                    botClient.sendMessage(
                        chatId,
                        "❌ Номер *$trimmedText* уже зарегистрирован!",
                        "markdown"
                    )
                    registrationState[userId] = false
                    return
                }

                try {
                    val savedUser = maxUserService.saveUser(trimmedText, userId, chatId)
                    registrationState[userId] = false

                    val userName = savedUser.userName ?: "Сотрудник"
                    log.info("✅ Пользователь $userName ($userId) зарегистрирован с номером $trimmedText")

                    val buttons = listOf(
                        listOf(
                            mapOf(
                                "type" to "callback",
                                "text" to "📋 Мой ID",
                                "payload" to "my_id"
                            )
                        )
                    )

                    botClient.sendMessageWithKeyboard(
                        userId,
                        """
                        👋 *Привет, $userName!*

                        ✅ Регистрация успешна!

                        📋 Ваш внутренний номер: *$trimmedText*
                        📋 Ваш user_id: `$userId`
                        📋 chat_id: `$chatId`
                        """.trimIndent(),
                        buttons,
                        "markdown"
                    )
                } catch (e: Exception) {
                    log.error("❌ Ошибка сохранения пользователя $userId", e)
                    botClient.sendMessage(
                        chatId,
                        "❌ Ошибка сохранения. Попробуйте позже.",
                        "markdown"
                    )
                    registrationState[userId] = false
                }
            } else {
                botClient.sendMessage(
                    chatId,
                    "❌ Введите только цифры, например: `101`",
                    "markdown"
                )
            }
            return
        }

        when (trimmedText.lowercase()) {
            "/start" -> {
                sendMainMenu(userId, chatId)
            }

            "/register" -> {
                registrationState[userId] = true
                botClient.sendMessage(
                    chatId,
                    """
                    📝 Введите ваш внутренний номер телефона (только цифры).
                    """.trimIndent(),
                    "markdown"
                )
            }

            "/id" -> {
                val user = maxUserService.findByUserId(userId)
                if (user != null) {
                    val userName = user.userName ?: "Сотрудник"
                    botClient.sendMessage(
                        chatId,
                        """
                        👤 *$userName*

                        📋 Ваш user_id: `$userId`
                        📋 chat_id: `$chatId`
                        📋 Внутренний номер: *${user.internalNumber}*
                        """.trimIndent(),
                        "markdown"
                    )
                } else {
                    botClient.sendMessage(
                        chatId,
                        """
                        📋 Ваш user_id: `$userId`
                        📋 chat_id: `$chatId`

                        Вы ещё не зарегистрированы. Напишите /start
                        """.trimIndent(),
                        "markdown"
                    )
                }
            }

            "/search" -> {
                botClient.sendMessage(
                    chatId,
                    """
            🔍 *Поиск сотрудников*

            Введите фамилию, имя, должность или отдел.

            Например:
            `/search Иванов`
            `/search Программист`
            `/search ИТ`
            """.trimIndent(),
                    "markdown"
                )
            }

            else -> {
                // ✅ Если сообщение начинается с "/search "
                if (trimmedText.startsWith("/search ", ignoreCase = true)) {
                    val query = trimmedText.substringAfter("/search ").trim()
                    if (query.isNotEmpty()) {
                        searchEmployees(chatId, query)
                    } else {
                        botClient.sendMessage(
                            chatId,
                            "❌ Введите поисковый запрос. Например: `/search Иванов`",
                            "markdown"
                        )
                    }
                } else {
                    sendMainMenu(userId, chatId)
                }
            }
        }
    }

    private fun searchEmployees(chatId: String, query: String) {
        val searchLower = query.lowercase()

        // Ищем в Phonebook
        val allEmployees = phonebookRepository.findAll()
        val results = allEmployees.filter { employee ->
            val fullName = listOfNotNull(employee.fname, employee.nname).joinToString(" ")

            fullName.lowercase().contains(searchLower) ||
                    employee.dolgnost?.lowercase()?.contains(searchLower) == true ||
                    employee.otdel?.lowercase()?.contains(searchLower) == true ||
                    employee.fname?.lowercase()?.contains(searchLower) == true ||
                    employee.nname?.lowercase()?.contains(searchLower) == true
        }.take(10) // ограничиваем 10 результатами

        if (results.isEmpty()) {
            botClient.sendMessage(
                chatId,
                "🔍 По запросу `$query` ничего не найдено.",
                "markdown"
            )
            return
        }

        // Формируем сообщение
        val message = buildSearchResult(results, query)
        botClient.sendMessage(chatId, message, "markdown")
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
}