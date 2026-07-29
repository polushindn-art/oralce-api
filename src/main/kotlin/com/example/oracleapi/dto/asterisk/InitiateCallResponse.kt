package com.example.oracleapi.dto.asterisk

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ на инициацию звонка")
data class InitiateCallResponse(
    @field:Schema(description = "Успешность операции", example = "true")
    val success: Boolean,

    @field:Schema(description = "Сообщение", example = "Звонок инициирован: 1001 → 89635328259")
    val message: String,

    @field:Schema(description = "Детали звонка")
    val data: CallDetails? = null
)

@Schema(description = "Детали звонка")
data class CallDetails(
    @field:Schema(description = "С какого номера", example = "1001")
    val from: String,

    @field:Schema(description = "На какой номер", example = "89635328259")
    val to: String,

    @field:Schema(description = "Имя звонящего", example = "Иванов Иван")
    val callerId: String?
)