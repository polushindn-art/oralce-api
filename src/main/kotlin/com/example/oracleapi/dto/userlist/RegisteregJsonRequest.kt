package com.example.oracleapi.dto.userlist

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Запрос на получение зарегистрированных ТСД")
class RegisteregJsonRequest(
    @param:Schema(description = "Серийный номер ТСД", example = "014.0000", required = true)
    val sn: String
)