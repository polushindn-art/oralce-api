package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@RestController
@RequestMapping("/v1/api/1c")
@Tag(name = "API Proxy 1C", description = "Прокси для внешних API 1C")
class ApiProxyController(
    private val restTemplateBuilder: RestTemplateBuilder
) {
    // Статистика
    private val requestCount = AtomicLong(0)
    private val successCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)
    private val totalTimeSum = AtomicLong(0)
    private val apiTimeSum = AtomicLong(0)
    private val startTime = System.currentTimeMillis()

    // Детальная статистика по методам
    private val methodStats = mutableMapOf<String, MethodStat>()

    data class MethodStat(
        val count: AtomicLong = AtomicLong(0),
        val successCount: AtomicLong = AtomicLong(0),
        val errorCount: AtomicLong = AtomicLong(0),
        val totalTimeSum: AtomicLong = AtomicLong(0),
        val apiTimeSum: AtomicLong = AtomicLong(0)
    )

    private val log = LoggerFactory.getLogger(javaClass)

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

    @GetMapping("/stats")
    @Operation(
        summary = "Статистика прокси",
        description = "Возвращает статистику работы прокси"
    )
    fun getStats(): ResponseEntity<Map<String, Any>> {
        val count = requestCount.get()
        val uptime = System.currentTimeMillis() - startTime

        // Статистика по методам
        val methodsStats = methodStats.map { (method, stat) ->
            val methodCount = stat.count.get()
            method to mapOf(
                "count" to methodCount,
                "success" to stat.successCount.get(),
                "error" to stat.errorCount.get(),
                "avg_total_time_ms" to if (methodCount > 0) stat.totalTimeSum.get() / methodCount else 0,
                "avg_api_time_ms" to if (methodCount > 0) stat.apiTimeSum.get() / methodCount else 0
            )
        }.toMap()

        val startDateTime = LocalDateTime.now().minusNanos((uptime * 1_000_000))

        val stats = mapOf(
            "uptime_seconds" to uptime / 1000,
            "uptime_human" to formatUptime(uptime),
            "start_time" to startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "current_time" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "total_requests" to count,
            "success_requests" to successCount.get(),
            "error_requests" to errorCount.get(),
            "avg_total_time_ms" to if (count > 0) totalTimeSum.get() / count else 0,
            "avg_api_time_ms" to if (count > 0) apiTimeSum.get() / count else 0,
            "proxy_overhead_ms" to if (count > 0) (totalTimeSum.get() - apiTimeSum.get()) / count else 0,
            "methods" to methodsStats
        )

        return ResponseEntity.ok(stats)
    }

    @GetMapping("/stats/reset")
    @Operation(
        summary = "Сброс статистики",
        description = "Сбрасывает всю накопленную статистику"
    )
    fun resetStats(): ResponseEntity<MyApiResponse<Unit>> {
        requestCount.set(0)
        successCount.set(0)
        errorCount.set(0)
        totalTimeSum.set(0)
        apiTimeSum.set(0)
        methodStats.clear()

        log.info("Proxy statistics has been reset")

        return ResponseEntity.ok(
            MyApiResponse.success(
                data = Unit,
                message = "Statistics has been reset"
            )
        )
    }

    private fun formatUptime(millis: Long): String {
        val seconds = millis / 1000
        val days = seconds / 86400
        val hours = (seconds % 86400) / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m ${secs}s"
            hours > 0 -> "${hours}h ${minutes}m ${secs}s"
            minutes > 0 -> "${minutes}m ${secs}s"
            else -> "${secs}s"
        }
    }

    private fun updateStats(
        method: String,
        totalTime: Long,
        apiTime: Long,
        isSuccess: Boolean
    ) {
        requestCount.incrementAndGet()
        totalTimeSum.addAndGet(totalTime)
        apiTimeSum.addAndGet(apiTime)

        if (isSuccess) {
            successCount.incrementAndGet()
        } else {
            errorCount.incrementAndGet()
        }

        // Статистика по методу
        val stat = methodStats.getOrPut(method) { MethodStat() }
        stat.count.incrementAndGet()
        stat.totalTimeSum.addAndGet(totalTime)
        stat.apiTimeSum.addAndGet(apiTime)

        if (isSuccess) {
            stat.successCount.incrementAndGet()
        } else {
            stat.errorCount.incrementAndGet()
        }
    }

    @RequestMapping("/**")
    @Operation(
        summary = "Прокси",
        description = "Перенаправляет запросы на другой API (192.168.12.189) с добавлением Basic Auth"
    )
    fun proxyRequest(
        request: HttpServletRequest
    ): ResponseEntity<*> {
        val totalStartTime = System.currentTimeMillis()

        // Получаем путь после /api/1c/
        val requestUri = request.requestURI
        val targetPath = requestUri.substringAfter("/api/1c/")

        // Исключаем эндпоинты статистики
        if (targetPath == "stats" || targetPath == "stats/reset") {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(MyApiResponse.unsuccess<Unit>(message = "Endpoint not found"))
        }

        // Проверяем, что путь не пустой
        if (targetPath.isBlank() || targetPath == "**") {
            val duration = System.currentTimeMillis() - totalStartTime
            log.warn("Empty path requested, proxy time: {}ms", duration)

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    MyApiResponse.unsuccess<Unit>(
                        message = "Please specify the target path. Example: /api/1c/UT_2025_TEST_1/hs/TokenRequest/UninvoicedGoods",
                        path = request.servletPath
                    )
                )
        }

        // Замеряем время на подготовку запроса
        val prepareStartTime = System.currentTimeMillis()

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
        val bodySize = body.size

        // Создаем запрос к целевому API
        val httpEntity = if (body.isNotEmpty()) {
            HttpEntity(body, headers)
        } else {
            HttpEntity<ByteArray>(headers)
        }

        val prepareTime = System.currentTimeMillis() - prepareStartTime

        return try {
            val method = HttpMethod.valueOf(request.method)

            // Логируем начало запроса
            log.info("→ {} {} | body: {} bytes | prepare: {}ms", method, targetPath, bodySize, prepareTime)

            // Замеряем время запроса к целевому API
            val apiStartTime = System.currentTimeMillis()
            val responseEntity = restTemplate.exchange<ByteArray>(
                targetUrl,
                method,
                httpEntity
            )
            val apiTime = System.currentTimeMillis() - apiStartTime

            val totalTime = System.currentTimeMillis() - totalStartTime
            val responseSize = responseEntity.body?.size ?: 0

            // Обновляем статистику
            updateStats(method.name(), totalTime, apiTime, true)

            // Логируем ответ с детальным временем
            log.info("← {} {} | {} | total: {}ms | api: {}ms | prepare: {}ms | response: {} bytes",
                method, targetPath, responseEntity.statusCode, totalTime, apiTime, prepareTime, responseSize)

            ResponseEntity
                .status(responseEntity.statusCode)
                .headers(responseEntity.headers)
                .body(responseEntity.body)

        } catch (e: HttpStatusCodeException) {
            val apiTime = System.currentTimeMillis() - prepareStartTime - prepareTime
            val totalTime = System.currentTimeMillis() - totalStartTime
            val responseSize = e.responseBodyAsByteArray?.size ?: 0

            // Обновляем статистику (ошибка)
            updateStats(request.method, totalTime, apiTime, false)

            // Логируем ошибку с детальным временем
            log.warn("← {} {} | {} | total: {}ms | api: {}ms | prepare: {}ms | error: {} | response: {} bytes",
                request.method, targetPath, e.statusCode, totalTime, apiTime, prepareTime, e.statusText, responseSize)

            ResponseEntity
                .status(e.statusCode)
                .headers(e.responseHeaders ?: HttpHeaders())
                .body(e.responseBodyAsByteArray)

        } catch (e: Exception) {
            val totalTime = System.currentTimeMillis() - totalStartTime

            // Обновляем статистику (ошибка)
            updateStats(request.method, totalTime, 0, false)

            // Логируем ошибку прокси
            log.error("✗ {} {} | total: {}ms | prepare: {}ms | proxy error: {}",
                request.method, targetPath, totalTime, prepareTime, e.message)

            ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(MyApiResponse.unsuccess<Unit>(message = "Proxy error: ${e.message}"))
        }
    }
}