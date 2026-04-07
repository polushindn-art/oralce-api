package com.example.oracleapi.dto.idhead.status

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ на обновление статуса документа")
data class StatusUpdateResponse(
    @Schema(description = "Успешность операции", example = "true")
    val success: Boolean,

    @Schema(description = "Сообщение о результате", example = "Статус документа успешно обновлен")
    val message: String,

    @Schema(description = "RN документа", example = "12345")
    val rn: Long,

    @Schema(description = "Новый статус", example = "2")
    val newStatus: Long
)