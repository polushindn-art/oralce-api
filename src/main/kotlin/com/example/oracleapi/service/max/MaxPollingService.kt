package com.example.oracleapi.service.max

import com.example.oracleapi.config.MaxApiProperties
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
@Profile("prod")
class MaxPollingService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties,
    private val botClient: MaxBotClient,
    private val maxUserService: MaxUserService
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
                val updateType = update["update_type"] as? String

                when (updateType) {
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
                        val callbackData = update["callback_data"] as? Map<*, *> ?: return@forEach
                        val userId = (callbackData["user_id"] as? Number)?.toString() ?: return@forEach
                        val chatId = (callbackData["chat_id"] as? Number)?.toString() ?: return@forEach
                        val payload = callbackData["payload"] as? String ?: return@forEach

                        log.info("🔘 Получен callback от user_id=$userId: payload=$payload")

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

    private fun handleCallback(userId: String, chatId: String, payload: String) {
        when (payload) {
            "register" -> {
                registrationState[userId] = true
                botClient.sendMessage(
                    chatId,
                    """
                    📝 Введите ваш внутренний номер телефона (только цифры).

                    Например: `101` или `1001`
                    """.trimIndent(),
                    "markdown"
                )
            }

            "my_id" -> {
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

            "change_number" -> {
                registrationState[userId] = true
                botClient.sendMessage(
                    chatId,
                    """
                    📝 Введите новый внутренний номер телефона (только цифры).

                    Например: `101` или `1001`
                    """.trimIndent(),
                    "markdown"
                )
            }

            "help" -> {
                botClient.sendMessage(
                    chatId,
                    """
                    📖 *Помощь*

                    /start — главное меню
                    /id — показать ваши ID
                    /register — зарегистрировать номер

                    Также вы можете использовать кнопки в меню.
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
            val number = trimmedText
            if (number.matches(Regex("^\\d+$"))) {
                val existingUser = maxUserService.findByInternalNumber(number)
                if (existingUser != null) {
                    botClient.sendMessage(
                        chatId,
                        "❌ Номер *$number* уже зарегистрирован!",
                        "markdown"
                    )
                    registrationState[userId] = false
                    return
                }

                try {
                    val savedUser = maxUserService.saveUser(number, userId, chatId)
                    registrationState[userId] = false

                    val userName = savedUser.userName ?: "Сотрудник"
                    log.info("✅ Пользователь $userName ($userId) зарегистрирован с номером $number")

                    val buttons = listOf(
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
                                "text" to "📝 Изменить номер",
                                "payload" to "change_number"
                            )
                        )
                    )

                    botClient.sendMessageWithKeyboard(
                        chatId,
                        """
                        👋 *Привет, $userName!*

                        ✅ Регистрация успешна!

                        📋 Ваш внутренний номер: *$number*
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
                val existingUser = maxUserService.findByUserId(userId)
                if (existingUser != null) {
                    val userName = existingUser.userName ?: "Сотрудник"
                    val buttons = listOf(
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
                                "text" to "📝 Изменить номер",
                                "payload" to "change_number"
                            )
                        )
                    )

                    botClient.sendMessageWithKeyboard(
                        chatId,
                        """
                        👋 *Привет, $userName!*

                        ✅ Вы уже зарегистрированы!

                        📋 Ваш внутренний номер: *${existingUser.internalNumber}*
                        📋 Ваш user_id: `$userId`
                        """.trimIndent(),
                        buttons,
                        "markdown"
                    )
                    return
                }

                val buttons = listOf(
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
                            "text" to "ℹ️ Помощь",
                            "payload" to "help"
                        )
                    )
                )

                botClient.sendMessageWithKeyboard(
                    chatId,
                    """
                    👋 *Добро пожаловать!*

                    Я бот для уведомлений о входящих звонках.

                    Нажмите кнопку *Регистрация*, чтобы привязать ваш внутренний номер.
                    """.trimIndent(),
                    buttons,
                    "markdown"
                )
            }

            "/register" -> {
                registrationState[userId] = true
                botClient.sendMessage(
                    chatId,
                    """
                    📝 Введите ваш внутренний номер телефона (только цифры).

                    Например: `101` или `1001`
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

            else -> {
                botClient.sendMessage(
                    chatId,
                    """
                    Доступные команды:
                    /start — главное меню
                    /register — зарегистрировать номер
                    /id — показать ваши ID
                    """.trimIndent()
                )
            }
        }
    }
}