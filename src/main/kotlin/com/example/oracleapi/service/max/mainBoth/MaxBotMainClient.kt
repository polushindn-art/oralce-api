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

@Service
class MaxBotMainClient(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Отправка сообщения (по chat_id)
     */
    fun sendMessage(chatId: String, text: String, format: String = "markdown"): Map<String, Any> {
        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("chat_id", chatId)
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botAuthToken)
            set("Content-Type", "application/json")
        }

        val body = mapOf(
            "text" to text,
            "format" to format
        )

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

    /**
     * Отправка сообщения с клавиатурой (по chat_id!)
     */
    fun sendMessageWithKeyboard(
        chatId: String,  // ← Было userId, стало chatId!
        text: String,
        buttons: List<List<Map<String, Any>>>,
        format: String = "markdown"
    ): Map<String, Any> {
        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("chat_id", chatId)  // ← Было user_id, стало chat_id!
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botAuthToken)
            set("Content-Type", "application/json")
        }

        val body = mapOf(
            "text" to text,
            "format" to format,
            "attachments" to listOf(
                mapOf(
                    "type" to "inline_keyboard",
                    "payload" to mapOf(
                        "buttons" to buttons
                    )
                )
            )
        )

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
}