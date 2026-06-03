package com.example.oracleapi.dto.orderhead.ins

@io.swagger.v3.oas.annotations.media.Schema(description = "Ответ после создания заказа")
data class OrderHeadInsResponse(
    @io.swagger.v3.oas.annotations.media.Schema(description = "Сгенерированный номер документа")
    val docnumb: java.math.BigDecimal? = null,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Сгенерированный RN записи")
    val rn: Long,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Время выполнения в миллисекундах")
    val executionTimeMs: Long,

    @io.swagger.v3.oas.annotations.media.Schema(description = "Временная метка операции")
    val timestamp: String = java.time.LocalDateTime.now().toString()
)