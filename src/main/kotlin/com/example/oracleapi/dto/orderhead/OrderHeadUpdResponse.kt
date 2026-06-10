package com.example.oracleapi.dto.orderhead

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

@Schema(description = "Ответ после обновления заказа")
data class OrderHeadUpdResponse(
    @Schema(description = "RN обновленной записи")
    val rn: Long,

    @Schema(description = "Номер документа")
    val docnumb: BigDecimal? = null,

    @Schema(description = "Время выполнения в миллисекундах")
    val executionTimeMs: Long,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Schema(description = "Временная метка операции")
    val timestamp: String = LocalDateTime.now().toString()
)