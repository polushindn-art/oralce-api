package com.example.oracleapi.dto.orderspec

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Ответ после создания спецификации")
data class OrderSpecInsResponse(
    @Schema(description = "Сгенерированный RN записи")
    val rn: Long,

    @Schema(description = "Время выполнения в миллисекундах")
    val executionTimeMs: Long,

    @Schema(description = "Временная метка операции")
    val timestamp: String = LocalDateTime.now().toString()
)