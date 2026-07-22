package com.example.oracleapi.service.max

import com.example.oracleapi.config.MaxApiProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class MaxBotClient(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    fun sendMessage(chatId: String, text: String, format: String = "markdown"): Map<String, Any> {
        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/messages")
            .queryParam("chat_id", chatId)
            .build()
            .toUri()

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botToken)
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

        // Приводим к нужному типу
        @Suppress("UNCHECKED_CAST")
        return rawResponse as Map<String, Any>
    }

    /**
     * Получение информации о боте
     * @return Map с данными бота (user_id, first_name, username, is_bot и т.д.)
     */
    fun getBotInfo(): Map<String, Any> {
        val url = "${properties.botApiUrl}/me"

        val headers = HttpHeaders().apply {
            set("Authorization", properties.botToken)
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