package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/v1/api")
class General : BaseController() {

    private val log = LoggerFactory.getLogger(General::class.java)

    /**
     * Проверка работоспособности API
     */
    @GetMapping("/health")
    fun healthCheck(request: HttpServletRequest): MyApiResponse<HealtResponse> {
        // Получаем реальный IP с учетом Nginx
        val clientIp = request.getHeader("X-Forwarded-For") ?: request.remoteAddr
        val userAgent = request.getHeader("User-Agent") ?: "Unknown"
        val referer = request.getHeader("Referer") ?: "Direct"

        log.info("🔍 Health check received from IP: $clientIp | User-Agent: $userAgent | Referer: $referer")

        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        return success(HealtResponse(
            "UP",
            "Oracle API",
            now.format(formatter),
            now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            System.currentTimeMillis(),
            java.time.ZoneId.systemDefault().toString()
        ))
    }

    data class HealtResponse (
        val status: String,
        val service: String,
        val timestamp: String,
        val timestampIso: String,
        val timestampMs: Long,
        val timezone: String
    )
}