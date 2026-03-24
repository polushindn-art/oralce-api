package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration
import java.util.*

@RestController
@RequestMapping("/api/proxy")
@Tag(name = "API Proxy", description = "Прокси для внешних API")
class ApiProxyController(
    private val restTemplateBuilder: RestTemplateBuilder
) {

    private val targetApiUrl = "http://192.168.12.189"
    private val targetApiUsername = "Администратор"
    private val targetApiPassword = "htrhtfwbz"

    private val basicAuthHeader: String by lazy {
        val credentials = "$targetApiUsername:$targetApiPassword"
        val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
        "Basic $encoded"
    }

    private val restTemplate: RestTemplate by lazy {
        restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(60))
            .build()
    }

    @RequestMapping("/**")
    @Operation(
        summary = "Прокси",
        description = "Перенаправляет запросы на другой API с добавлением Basic Auth"
    )
    fun proxyRequest(
        request: HttpServletRequest
    ): ResponseEntity<*> {

        // Получаем путь после /api/proxy/
        val requestUri = request.requestURI
        val targetPath = requestUri.substringAfter("/api/proxy/")

        // Формируем query параметры для URL
        val queryParams: MultiValueMap<String, String> = LinkedMultiValueMap()
        request.parameterNames.asSequence().forEach { paramName ->
            request.getParameterValues(paramName)?.let { values ->
                queryParams[paramName] = values.toList()
            }
        }

        // Строим целевой URL
        val targetUrl = UriComponentsBuilder
            .fromHttpUrl("$targetApiUrl/$targetPath")
            .queryParams(queryParams)
            .build()
            .toUriString()

        println("=== Proxy ===")
        println("Method: ${request.method}")
        println("URL: $targetUrl")

        // Копируем заголовки
        val headers = HttpHeaders()
        request.headerNames.asSequence().forEach { headerName ->
            when (headerName.lowercase()) {
                "host", "content-length", "authorization", "connection" -> {
                    // Пропускаем
                }
                else -> {
                    headers[headerName] = request.getHeaders(headerName).toList()
                }
            }
        }

        // Добавляем Basic Auth
        headers["Authorization"] = basicAuthHeader

        // Получаем тело запроса
        val body = request.inputStream.readBytes()

        if (body.isNotEmpty()) {
            println("Body: ${String(body, Charsets.UTF_8)}")
        }
        println("==============")

        // Создаем запрос к целевому API
        val httpEntity = if (body.isNotEmpty()) {
            HttpEntity(body, headers)
        } else {
            HttpEntity<ByteArray>(headers)
        }

        return try {
            val method = HttpMethod.valueOf(request.method)
            val responseEntity = restTemplate.exchange(
                targetUrl,
                method,
                httpEntity,
                ByteArray::class.java
            )

            ResponseEntity
                .status(responseEntity.statusCode)
                .headers(responseEntity.headers)
                .body(responseEntity.body)

        } catch (e: HttpStatusCodeException) {
            ResponseEntity
                .status(e.statusCode)
                .headers(e.responseHeaders ?: HttpHeaders())
                .body(e.responseBodyAsByteArray)

        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error<Unit>(message = "Proxy error: ${e.message}"))
        }
    }
}