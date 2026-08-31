package com.example.oracleapi.service.max.mainBoth

import com.example.oracleapi.config.MaxApiProperties
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URLDecoder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component

@Component
class MaxBotMainClient(
    private val restTemplate: RestTemplate,
    private val properties: MaxApiProperties
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    // ==================== ОСНОВНЫЕ МЕТОДЫ ====================

    fun sendMessage(chatId: String, text: String, format: String = "markdown"): Map<String, Any> {
        return sendMessageWithInlineKeyboard(chatId, text, emptyList(), format)
    }

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

    fun getBotInfo(): Map<String, Any> {
        val uri = buildMeUri()
        return executeGet(uri)
    }

    // ==================== ОТПРАВКА ФОТО ====================

    fun sendPhoto(
        chatId: String,
        photoBytes: ByteArray,
        fileName: String,
        caption: String,
        buttons: List<List<Map<String, Any>>> = emptyList() // ← Добавили параметр
    ): Map<String, Any> {
        log.info("📸 [MAX API] ========== НАЧАЛО ОТПРАВКИ ФОТО ==========")
        log.info("📸 [MAX API] chatId: {}", chatId)
        log.info("📸 [MAX API] fileName: {}", fileName)
        log.info("📸 [MAX API] Размер фото: {} байт", photoBytes.size)

        val uploadUrl = getUploadUrl()
        val imageToken = uploadFileToUrl(uploadUrl, photoBytes, fileName)

        val maxAttempts = 5
        var lastException: Exception? = null

        for (attempt in 1..maxAttempts) {
            try {
                val delay = when (attempt) {
                    1 -> 500L
                    2 -> 1000L
                    3 -> 2000L
                    4 -> 4000L
                    else -> 5000L
                }

                Thread.sleep(delay)

                // Передаем кнопки в метод отправки
                val response = sendMessageWithAttachmentToken(chatId, caption, imageToken, buttons)

                log.info("✅ [MAX API] Фото '{}' успешно отправлено в чат {} (попытка {})", fileName, chatId, attempt)
                return response

            } catch (e: HttpServerErrorException.InternalServerError) {
                lastException = e
                if (attempt == maxAttempts) throw e
            } catch (e: Exception) {
                lastException = e
                throw e
            }
        }

        throw lastException ?: RuntimeException("Неизвестная ошибка при отправке фото")
    }

    // ==================== ПРИВАТНЫЕ МЕТОДЫ ====================

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

    // ==================== ЗАГРУЗКА ФАЙЛОВ ====================

    private fun getUploadUrl(): String {
        log.info("📤 [MAX API] getUploadUrl()")

        val uri = UriComponentsBuilder.fromHttpUrl("${properties.botApiUrl}/uploads")
            .queryParam("type", "image")
            .build()
            .toUri()

        log.info("📤 [MAX API] URI: {}", uri)

        val response = executePost(uri, emptyMap())
        log.info("📦 [MAX API] Ответ от /uploads: {}", response)

        val url = response["url"] as? String
        if (url == null) {
            log.error("❌ [MAX API] В ответе нет поля 'url'! Полный ответ: {}", response)
            throw RuntimeException("Сервер MAX не вернул URL для загрузки")
        }

        log.info("✅ [MAX API] URL получен: {}", url)
        return url
    }


    private fun uploadFileToUrl(uploadUrl: String, bytes: ByteArray, fileName: String): String {
        log.info("📤 [MAX API] Загрузка через OkHttp Multipart + умный парсер photos")

        // 1. Формируем запрос через OkHttp (который стабильно дает HTTP 200 на oneme.ru)
        val requestBody = okhttp3.MultipartBody.Builder()
            .setType(okhttp3.MultipartBody.FORM)
            .addFormDataPart(
                "data",
                fileName,
                bytes.toRequestBody("image/png".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val response = client.newCall(request).execute()
        val responseBodyString = response.body?.string()
        log.info("📦 [MAX API] Код: {}, Тело: {}", response.code, responseBodyString)

        if (!response.isSuccessful || responseBodyString.isNullOrBlank()) {
            throw RuntimeException("Ошибка загрузки HTTP ${response.code}: $responseBodyString")
        }

        val json = JSONObject(responseBodyString)

        // 2. Проверяем ошибки верхнего уровня, если они есть
        if (json.has("error_code")) {
            val errCode = json.optString("error_code")
            val errData = json.optString("error_data")
            if (errCode.isNotEmpty() && errCode != "0" && errCode != "null" && errCode != "4") {
                throw RuntimeException("Ошибка загрузки: error_code=$errCode, data=$errData")
            }
        }

        // 3. УМНЫЙ ПАРСЕР: Достаем токен из вложенной структуры `photos -> <id> -> token`
        if (json.has("photos")) {
            val photosObj = json.optJSONObject("photos")
            if (photosObj != null && photosObj.keys().hasNext()) {
                val firstKey = photosObj.keys().next()
                val photoData = photosObj.optJSONObject(firstKey)
                val token = photoData?.optString("token")
                if (!token.isNullOrBlank()) {
                    log.info("✅ [MAX API] Токен успешно извлечен из структуры photos!")
                    return token
                }
            }
        }

        // 4. Запасной вариант: проверяем прямые поля на случай другого формата ответа
        val directToken = json.optString("token", null)?.takeIf { it.isNotBlank() && it != "null" }
            ?: json.optString("file_id", null)?.takeIf { it.isNotBlank() && it != "null" }

        if (directToken != null) {
            return directToken
        }

        // 5. Последний фоллбек — достаем из URL
        val uriComponents = UriComponentsBuilder.fromHttpUrl(uploadUrl).build()
        val rawToken = uriComponents.queryParams.getFirst("photoIds")
            ?: uriComponents.queryParams.getFirst("token")
        if (rawToken != null) {
            return URLDecoder.decode(rawToken, "UTF-8")
        }

        throw RuntimeException("Не удалось получить токен из ответа: $responseBodyString")
    }

    private fun sendMessageWithAttachmentToken(
        chatId: String,
        text: String,
        token: String,
        buttons: List<List<Map<String, Any>>>
    ): Map<String, Any> {
        val uri = buildMessagesUri(chatId)

        // Формируем вложения: картинка + клавиатура (если она есть)
        val attachments = mutableListOf<Map<String, Any>>(
            mapOf(
                "type" to "image",
                "payload" to mapOf("token" to token)
            )
        )

        if (buttons.isNotEmpty()) {
            attachments.add(
                mapOf(
                    "type" to "inline_keyboard",
                    "payload" to mapOf("buttons" to buttons)
                )
            )
        }

        val body = mutableMapOf(
            "text" to text,
            "format" to "markdown",
            "attachments" to attachments
        )

        return executePost(uri, body)
    }

    private fun extractErrorId(responseBody: String?): String? {
        if (responseBody == null) return null
        return try {
            val json = JSONObject(responseBody)
            val message = json.optString("message", "")
            val match = Regex("Error ID: ([a-f0-9]+)").find(message)
            match?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}