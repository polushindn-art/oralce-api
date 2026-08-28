package com.example.oracleapi.dto.idhead.del

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Schema(description = "Запрос на удаление документа")
data class IdHeadDeleteRequest(
    @field:Schema(description = "RN документа", example = "12345")
    @field:NotNull(message = "RN документа обязателен")
    @field:Positive(message = "RN документа должен быть положительным числом")
    var rn: Long,
    @field:Schema(description = "Обновить статус перед удалением", example = "false")
    val isUpdate: Boolean = false
)