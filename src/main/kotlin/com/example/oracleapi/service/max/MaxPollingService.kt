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

                if (updateType != "message_created") return@forEach

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

        } catch (_: ResourceAccessException) {
            // таймаут — нормально
        } catch (e: Exception) {
            log.error("❌ Ошибка в Long Polling", e)
        }
    }

    private fun handleMessage(userId: String, chatId: String, text: String) {
        val trimmedText = text.trim()

        // Проверяем, находится ли пользователь в процессе регистрации
        if (registrationState[userId] == true) {
            val number = trimmedText
            if (number.matches(Regex("^\\d+$"))) {
                // Проверяем, есть ли уже такой внутренний номер в БД
                val existingUser = maxUserService.findByInternalNumber(number)
                if (existingUser != null) {
                    botClient.sendMessage(
                        chatId,
                        """
                        ❌ Номер *$number* уже зарегистрирован!

                        Если это ваш номер, обратитесь к администратору.
                        """.trimIndent(),
                        "markdown"
                    )
                    registrationState[userId] = false
                    return
                }

                // Сохраняем в БД
                try {
                    val savedUser = maxUserService.saveUser(number, userId, chatId)
                    registrationState[userId] = false

                    // Получаем имя из сохранённого пользователя
                    val userName = savedUser.userName ?: "Сотрудник"

                    log.info("✅ Пользователь $userName ($userId) зарегистрирован с номером $number")

                    botClient.sendMessage(
                        chatId,
                        """
                        👋 *Привет, $userName!*

                        ✅ Регистрация успешна!

                        📋 Ваш внутренний номер: *$number*
                        📋 Ваш user_id: `$userId`
                        📋 chat_id: `$chatId`

                        Теперь вы будете получать уведомления о входящих звонках.
                        """.trimIndent(),
                        "markdown"
                    )
                } catch (e: Exception) {
                    log.error("❌ Ошибка сохранения пользователя $userId", e)
                    botClient.sendMessage(
                        chatId,
                        "❌ Ошибка сохранения. Попробуйте позже или обратитесь к администратору.",
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
            "/start", "/register" -> {
                // Проверяем, не зарегистрирован ли уже пользователь
                val existingUser = maxUserService.findByUserId(userId)
                if (existingUser != null) {
                    val userName = existingUser.userName ?: "Сотрудник"
                    botClient.sendMessage(
                        chatId,
                        """
                        👋 *Привет, $userName!*

                        ✅ Вы уже зарегистрированы!

                        📋 Ваш внутренний номер: *${existingUser.internalNumber}*
                        📋 Ваш user_id: `$userId`

                        Чтобы изменить номер, напишите /register и введите новый.
                        """.trimIndent(),
                        "markdown"
                    )
                    return
                }

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

                        Вы ещё не зарегистрированы. Напишите /register
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
                    /register — зарегистрировать внутренний номер
                    /id — показать ваши ID
                    """.trimIndent()
                )
            }
        }
    }
}