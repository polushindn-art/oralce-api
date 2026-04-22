package com.example.oracleapi.dto.tsdlist

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Активные пользователи ТСД")
data class UsedJson(
    @field:Schema(description = "Код пользователя")
    val usercode: String?,

    @field:Schema(description = "ФИО")
    val agnname: String?,

    @field:Schema(description = "Серийный номер ТСД")
    val sn: String?,

    @field:Schema(description = "ID устройства")
    val deviceid: String?,

    @field:Schema(description = "Время начала сессии")
    val timestart: String?
)