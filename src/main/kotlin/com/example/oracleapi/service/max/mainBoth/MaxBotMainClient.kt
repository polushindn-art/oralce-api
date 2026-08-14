package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.config.MaxApiProperties
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class MaxBotMainClient(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    private val log = LoggerFactory.getLogger(this::class.java)
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

    /**
     * Отправка изображения (в виде массива байтов) в чат
     */
    /**
     * Отправка изображения (в виде массива байтов) в чат
     */
    /**
     * Отправка изображения по правилам MAX API (через предварительную загрузку в /uploads)
     */
    /**
     * Отправка изображения через предварительную загрузку в /uploads (в виде сырых байтов)
     */
    fun sendPhoto(chatId: String, photoBytes: ByteArray, fileName: String, caption: String = ""): Map<String, Any> {
        // Шаг 1: Получаем URL для загрузки
        val uploadInitUri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/uploads")
            .queryParam("type", "image")
            .build()
            .toUri()

        val headers = createHeaders()
        val initResponse = restTemplate.exchange(
            uploadInitUri,
            HttpMethod.POST,
            HttpEntity<Nothing>(headers),
            Map::class.java
        )

        val uploadUrl = (initResponse.body?.get("url") as? String)
            ?: throw RuntimeException("Не удалось получить URL для загрузки от MAX API. Ответ: ${initResponse.body}")

        // Шаг 2: Загружаем файл как чистые бинарные данные (Raw Binary) в теле запроса
        val uploadHeaders = HttpHeaders().apply {
            contentType = MediaType.IMAGE_PNG
        }

        val uploadRequest = HttpEntity(photoBytes, uploadHeaders)
        val uploadResponse = restTemplate.exchange(
            uploadUrl,
            HttpMethod.POST,
            uploadRequest,
            Map::class.java
        )

        log.info("📦 [MAX API] Ответ от сервера загрузки файлов: ${uploadResponse.body}")

        val token = (uploadResponse.body?.get("token") as? String)
            ?: throw RuntimeException("Не удалось получить токен загруженного файла от MAX API. Ответ: ${uploadResponse.body}")

        // Шаг 3: Отправляем сообщение с вложением (attachment) через стандартный JSON API
        val messageUri = buildMessagesUri(chatId)
        val messageBody = mutableMapOf<String, Any>(
            "text" to caption,
            "format" to "markdown",
            "attachments" to listOf(
                mapOf(
                    "type" to "image",
                    "payload" to mapOf(
                        "token" to token
                    )
                )
            )
        )

        val messageHeaders = createHeaders().apply {
            set("Content-Type", "application/json")
        }

        val response = restTemplate.exchange(
            messageUri,
            HttpMethod.POST,
            HttpEntity(messageBody, messageHeaders),
            Map::class.java
        )

        @Suppress("UNCHECKED_CAST")
        return response.body as? Map<String, Any>
            ?: throw RestClientException("Empty response from MAX API")
    }

}