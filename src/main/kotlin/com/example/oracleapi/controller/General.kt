package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/v1/api")
class General : BaseController() {
    /**
     * Проверка работоспособности API
     */
    @GetMapping("/health")
    fun healthCheck(): MyApiResponse<HealtResponse> {
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