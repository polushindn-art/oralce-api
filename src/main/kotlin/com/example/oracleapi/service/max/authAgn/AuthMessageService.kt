package com.example.oracleapi.service.max.authAgn

import com.example.oracleapi.dto.max.MessageResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthMessageService(
    private val botAuthClient: MaxBotAuthClient
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun sendMessage(chatId: String, text: String, format: String = "markdown"): MessageResponse {
        return try {
            val response = botAuthClient.sendMessage(chatId, text, format)
            val messageId = extractMessageId(response)

            log.info("✅ [Auth Bot] Сообщение отправлено в chatId: $chatId")

            MessageResponse(
                success = true,
                messageId = messageId,
                error = null
            )
        } catch (e: Exception) {
            log.error("❌ [Auth Bot] Ошибка отправки", e)
            MessageResponse(
                success = false,
                messageId = null,
                error = "Ошибка отправки: ${e.message}"
            )
        }
    }

    private fun extractMessageId(response: Map<String, Any>): String {
        (response["message"] as? Map<*, *>)?.let { message ->
            (message["body"] as? Map<*, *>)?.get("mid")?.toString()?.let { return it }
        }
        return response["message_id"]?.toString() ?: "unknown"
    }
}