package com.example.oracleapi.dto.tsdlist

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Данные активных пользователй")
data class UsedJson (
    @field:Schema(description = "RN пользователя в UserList")
    val userList: Long?,
    @field:Schema(description = "Имя пользователя")
    val agnCode: String?,
    @field:Schema(description = "серийный номер ТСД")
    val sn: String?
)