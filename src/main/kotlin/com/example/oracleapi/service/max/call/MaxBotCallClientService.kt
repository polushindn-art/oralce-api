package com.example.oracleapi.service.max.call

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
class MaxBotCallClientService(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Отправка сообщения по chat_id (для групповых чатов)
     */
    fun sendMessage(chatId: String, text: String, format: String = "markdown"): Map<String, Any> {
        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("chat_id", chatId)
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botCallToken)
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

        val rawResponse = response.body ?: throw RestClientException("Empty response from MAX API")

        @Suppress("UNCHECKED_CAST")
        return rawResponse as Map<String, Any>
    }

    /**
     * Отправка сообщения по user_id (для личных диалогов)
     */
    fun sendMessageByUserId(userId: String, text: String, format: String = "markdown"): Map<String, Any> {
        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("user_id", userId)
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botCallToken)
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

        val rawResponse = response.body ?: throw RestClientException("Empty response from MAX API")

        @Suppress("UNCHECKED_CAST")
        return rawResponse as Map<String, Any>
    }

    /**
     * Отправка сообщения с инлайн-клавиатурой по user_id (для личных диалогов)
     */
    fun sendMessageWithKeyboard(
        userId: String,
        text: String,
        buttons: List<List<Map<String, Any>>>,
        format: String = "markdown"
    ): Map<String, Any> {
        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("user_id", userId)
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botCallToken)
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

        val rawResponse = response.body ?: throw RestClientException("Empty response from MAX API")

        @Suppress("UNCHECKED_CAST")
        return rawResponse as Map<String, Any>
    }

    /**
     * Получение информации о боте
     */
    fun getBotInfo(): Map<String, Any> {
        val url = "${properties.botApiUrl}/me"

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botCallToken)
            set("Content-Type", "application/json")
        }

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            HttpEntity<Nothing>(headers),
            Map::class.java
        )

        val rawResponse = response.body ?: throw RestClientException("Empty response from MAX API")

        @Suppress("UNCHECKED_CAST")
        return rawResponse as Map<String, Any>
    }
}