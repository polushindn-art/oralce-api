package com.example.oracleapi.dto.idhead.del

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Schema(description = "Запрос на удаление документа")
data class IdHeadDeleteRequest(
    @Schema(description = "RN документа", example = "12345")
    @field:NotNull(message = "RN документа обязателен")
    @field:Positive(message = "RN документа должен быть положительным числом")
    val rn: Long,

    @Schema(description = "Обновить статус перед удалением", example = "false")
    val isUpdate: Boolean = false
)