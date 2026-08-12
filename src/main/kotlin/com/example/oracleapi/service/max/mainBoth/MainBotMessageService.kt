package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.dto.max.MessageResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MainBotMessageService(
    private val botClient: MaxBotMainClient
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun sendMessage(chatId: String, text: String, format: String = "markdown"): MessageResponse {
        return try {
            val response = botClient.sendMessage(chatId, text, format)
            val messageId = extractMessageId(response)

            log.info("✅ [Main Bot] Сообщение отправлено в chatId: $chatId")

            MessageResponse(
                success = true,
                messageId = messageId,
                error = null
            )
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка отправки в chatId: $chatId", e)
            MessageResponse(
                success = false,
                messageId = null,
                error = "Ошибка отправки: ${e.message}"
            )
        }
    }

    fun sendMessageWithInlineKeyboard(
        chatId: String,
        text: String,
        buttons: List<List<Map<String, Any>>>,
        format: String = "markdown"
    ): MessageResponse {
        return try {
            val response = botClient.sendMessageWithInlineKeyboard(chatId, text, buttons, format)
            val messageId = extractMessageId(response)

            log.info("✅ [Main Bot] Сообщение с inline-кнопками отправлено в chatId: $chatId")

            MessageResponse(
                success = true,
                messageId = messageId,
                error = null
            )
        } catch (e: Exception) {
            log.error("❌ [Main Bot] Ошибка отправки с inline-кнопками в chatId: $chatId", e)
            MessageResponse(
                success = false,
                messageId = null,
                error = "Ошибка отправки: ${e.message}"
            )
        }
    }

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

    private fun extractMessageId(response: Map<String, Any>): String {
        (response["message"] as? Map<*, *>)?.let { message ->
            (message["body"] as? Map<*, *>)?.get("mid")?.toString()?.let { return it }
        }
        return response["message_id"]?.toString() ?: "unknown"
    }
}