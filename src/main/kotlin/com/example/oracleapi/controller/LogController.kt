package com.example.oracleapi.controller

import com.example.oracleapi.service.RecentLogBuffer
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/logs")
@Tag(name = "Лог", description = "Логи API")
class LogController(
    private val logBufferService: RecentLogBuffer
) {

    @GetMapping("/recent")
    @Operation(summary = "Получить логи")
    fun getRecentLogs(): List<String> {
        return logBufferService.getRecentLogs()
    }
}