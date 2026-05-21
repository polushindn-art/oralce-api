package com.example.oracleapi.dto.orderhead

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Ответ после создания заказа")
data class OrderHeadInsResponse(
    @Schema(description = "Сгенерированный номер документа")
    val docnumb: BigDecimal,

    @Schema(description = "Сгенерированный RN записи")
    val rn: Long,

    @Schema(description = "Время выполнения в миллисекундах")
    val executionTimeMs: Long,

    @Schema(description = "Временная метка операции")
    val timestamp: String = java.time.LocalDateTime.now().toString()
)