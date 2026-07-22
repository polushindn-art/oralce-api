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
    private val botClient: MaxBotClient
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private var marker: Long? = null

    private val registrationState = mutableMapOf<String, Boolean>()
    private val userNumberMap = mutableMapOf<String, String>()

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
                userNumberMap[userId] = number
                registrationState[userId] = false
                log.info("✅ Пользователь $userId зарегистрирован с номером $number")

                botClient.sendMessage(
                    chatId,
                    """
                    ✅ Номер *$number* сохранен!

                    📋 Ваш user_id: `$userId`
                    📋 Ваш chat_id: `$chatId`

                    Теперь вы будете получать уведомления о звонках.
                """.trimIndent(),
                    "markdown"
                )
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
                registrationState[userId] = true
                botClient.sendMessage(
                    chatId,
                    "📝 Введите ваш внутренний номер телефона (только цифры):",
                    "markdown"
                )
            }
            "/id" -> {
                botClient.sendMessage(
                    chatId,
                    "📋 Ваш user_id: `$userId`\n📋 chat_id: `$chatId`",
                    "markdown"
                )
            }
            else -> {
                botClient.sendMessage(
                    chatId,
                    "Напишите /register чтобы зарегистрировать номер"
                )
            }
        }
    }
}