package com.example.oracleapi.dto.idhead.status

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Schema(description = "Запрос на обновление статуса документа")
data class StatusUpdateRequest(
    @Schema(description = "RN документа", example = "12345")
    @field:NotNull(message = "RN документа обязателен")
    @field:Positive(message = "RN документа должен быть положительным числом")
    val rn: Long,

    @Schema(description = "Новый статус", example = "2")
    @field:NotNull(message = "Статус обязателен")
    val newStatus: Long
)