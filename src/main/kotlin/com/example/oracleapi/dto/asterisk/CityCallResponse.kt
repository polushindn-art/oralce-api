package com.example.oracleapi.dto.asterisk

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ на инициацию звонка с городского номера")
data class CityCallResponse(
    @field:Schema(description = "Успешность операции", example = "true")
    val success: Boolean,

    @field:Schema(description = "Сообщение", example = "Звонок на 89635328259 инициирован с городского номера")
    val message: String,

    @field:Schema(description = "Детали звонка")
    val data: CityCallDetails? = null
)

@Schema(description = "Детали звонка с городского номера")
data class CityCallDetails(
    @field:Schema(description = "Канал (транк)", example = "PJSIP/DIANET_388585")
    val channel: String,

    @field:Schema(description = "На какой номер звоним", example = "89635328259")
    val to: String,

    @field:Schema(description = "Имя звонящего (CallerID)", example = "Городской номер")
    val callerId: String?
)