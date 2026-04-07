package com.example.oracleapi.dto.idhead.prihod

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ на создание приходного ордера")
data class PrihodResponse(
    @Schema(description = "Успешность операции", example = "true")
    val success: Boolean,

    @Schema(description = "Сообщение о результате", example = "Приходный ордер успешно создан")
    val message: String,

    @Schema(description = "RN созданного документа", example = "123456789")
    val idheadRn: Long? = null
)