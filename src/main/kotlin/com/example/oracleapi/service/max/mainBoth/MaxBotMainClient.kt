package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.config.MaxApiProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class MaxBotMainClient(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    /**
     * Отправка сообщения (по chat_id) — без кнопок
     */
    fun sendMessage(chatId: String, text: String, format: String = "markdown"): Map<String, Any> {
        return sendMessageWithInlineKeyboard(chatId, text, emptyList(), format)
    }

    /**
     * Отправка сообщения с inline-клавиатурой (внутри сообщения, по chat_id)
     */
    fun sendMessageWithInlineKeyboard(
        chatId: String,
        text: String,
        buttons: List<List<Map<String, Any>>>,
        format: String = "markdown"
    ): Map<String, Any> {
        val body = buildMessageBody(text, format, buttons)
        val uri = buildMessagesUri(chatId)
        return executePost(uri, body)
    }

    /**
     * Получение информации о боте
     */
    fun getBotInfo(): Map<String, Any> {
        val uri = buildMeUri()
        return executeGet(uri)
    }

    // ========== PRIVATE METHODS ==========

    private fun createHeaders(): HttpHeaders {
        return HttpHeaders().apply {
            set("Authorization", properties.botMainToken)
            set("Content-Type", "application/json")
        }
    }

    private fun buildMessagesUri(chatId: String): URI {
        return UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("chat_id", chatId)
            .build()
            .toUri()
    }

    private fun buildMeUri(): URI {
        return UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/me")
            .build()
            .toUri()
    }

    private fun buildMessageBody(
        text: String,
        format: String,
        buttons: List<List<Map<String, Any>>>
    ): Map<String, Any> {
        val body = mutableMapOf<String, Any>(
            "text" to text,
            "format" to format
        )

        if (buttons.isNotEmpty()) {
            body["attachments"] = listOf(
                mapOf(
                    "type" to "inline_keyboard",
                    "payload" to mapOf(
                        "buttons" to buttons
                    )
                )
            )
        }

        return body
    }

    private fun executePost(uri: URI, body: Map<String, Any>): Map<String, Any> {
        val headers = createHeaders()
        val response = restTemplate.exchange(
            uri,
            HttpMethod.POST,
            HttpEntity(body, headers),
            Map::class.java
        )

        @Suppress("UNCHECKED_CAST")
        return response.body as? Map<String, Any>
            ?: throw RestClientException("Empty response from MAX API")
    }

    private fun executeGet(uri: URI): Map<String, Any> {
        val headers = createHeaders()
        val response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            HttpEntity<Nothing>(headers),
            Map::class.java
        )

        @Suppress("UNCHECKED_CAST")
        return response.body as? Map<String, Any>
            ?: throw RestClientException("Empty response from MAX API")
    }
}