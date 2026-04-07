package com.example.oracleapi.dto.idhead.del

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ на удаление документа")
data class IdHeadDeleteResponse(
    @Schema(description = "Успешность операции", example = "true")
    val success: Boolean,

    @Schema(description = "Сообщение о результате", example = "Документ успешно удален")
    val message: String,

    @Schema(description = "RN удаленного документа", example = "12345")
    val rn: Long
)