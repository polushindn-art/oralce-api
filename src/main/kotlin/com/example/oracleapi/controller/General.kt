package com.example.oracleapi.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("api")
class General {
    /**
     * Проверка работоспособности API
     */
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "service" to "Oracle API",
                "timestamp" to now.format(formatter),
                "timestamp_iso" to now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "timestamp_ms" to System.currentTimeMillis(),
                "timezone" to java.time.ZoneId.systemDefault().toString()
            )
        )
    }
}