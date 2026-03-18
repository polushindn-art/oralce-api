package com.example.oracleapi.dto.tsdlist

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Данные зарегистрированного ТСД")
data class Registeredjson(
    @field:Schema(description = "Серийный номер устройства")
    val sn: String?,

    @field:Schema(description = "Время начала сессии", example = "2024-11-19 09:22:52")
    val timestart: String?,

    @field:Schema(description = "Код пользователя")
    val usercode: String?,

    @field:Schema(description = "ФИО")
    val agnname: String?,

    @field:Schema(description = "Пароль")
    val parole: String?,

    @field:Schema(description = "PIN код пользователя")
    val pin: Long?,

    @field:Schema(description = "ПБЕ")
    val pbecode: String?,

    @field:Schema(description = "Склад")
    val store: List<StoreInfo>?
)

@Schema(description = "Информация о магазине")
data class StoreInfo(
    @field:Schema(description = "RN магазина")
    val rn: Long?,

    @field:Schema(description = "Код магазина")
    val storecode: String?
)