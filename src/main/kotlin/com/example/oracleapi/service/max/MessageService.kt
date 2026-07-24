package com.example.oracleapi.service.max

import com.example.oracleapi.dto.max.MessageResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val botClient: MaxBotClient
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Отправка сообщения по chat_id (для групповых чатов)
     */
    fun sendMessage(chatId: String, text: String, format: String = "markdown"): MessageResponse {
        return try {
            val response = botClient.sendMessage(chatId, text, format)
            val messageId = response["message_id"]?.toString()
                ?: response["id"]?.toString()
                ?: "unknown"

            log.info("✅ Сообщение отправлено в chatId: $chatId")
            MessageResponse(
                success = true,
                messageId = messageId,
                error = null
            )
        } catch (e: Exception) {
            log.error("❌ Ошибка отправки сообщения в chatId: $chatId", e)
            MessageResponse(
                success = false,
                messageId = null,
                error = "Ошибка отправки: ${e.message}"
            )
        }
    }

    /**
     * Отправка сообщения по user_id (для личных диалогов)
     */
    fun sendMessageByUserId(userId: String, text: String, format: String = "markdown"): MessageResponse {
        return try {
            val response = botClient.sendMessageByUserId(userId, text, format)
            val messageId = response["message_id"]?.toString()
                ?: response["id"]?.toString()
                ?: "unknown"

            log.info("✅ Сообщение отправлено пользователю userId: $userId")
            MessageResponse(
                success = true,
                messageId = messageId,
                error = null
            )
        } catch (e: Exception) {
            log.error("❌ Ошибка отправки сообщения пользователю userId: $userId", e)
            MessageResponse(
                success = false,
                messageId = null,
                error = "Ошибка отправки: ${e.message}"
            )
        }
    }

    /**
     * Отправка сообщения с инлайн-клавиатурой по user_id (для личных диалогов)
     */
    fun sendMessageWithKeyboard(
        userId: String,
        text: String,
        buttons: List<List<Map<String, Any>>>,
        format: String = "markdown"
    ): MessageResponse {
        return try {
            val response = botClient.sendMessageWithKeyboard(userId, text, buttons, format)
            val messageId = response["message_id"]?.toString()
                ?: response["id"]?.toString()
                ?: "unknown"

            log.info("✅ Сообщение с кнопками отправлено пользователю userId: $userId")
            MessageResponse(
                success = true,
                messageId = messageId,
                error = null
            )
        } catch (e: Exception) {
            log.error("❌ Ошибка отправки сообщения с кнопками пользователю userId: $userId", e)
            MessageResponse(
                success = false,
                messageId = null,
                error = "Ошибка отправки: ${e.message}"
            )
        }
    }

    /**
     * Получение информации о боте
     */
    fun getBotInfo(): Map<String, Any> {
        return try {
            val info = botClient.getBotInfo()
            log.info("✅ Информация о боте получена: ${info["first_name"]} (${info["username"]})")
            info
        } catch (e: Exception) {
            log.error("❌ Ошибка получения информации о боте", e)
            throw e
        }
    }
}