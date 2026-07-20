package com.example.oracleapi.dto.protocolMark

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class ProtocolMarkRequest(
    @field:NotNull(message = "tableRn не может быть null")
    @field:Min(value = 1, message = "tableRn должен быть больше 0")
    val tableRn: Long,

    @field:NotNull(message = "action не может быть null")
    @field:Min(value = 1, message = "action должен быть от 1 до 5")
    @field:Max(value = 5, message = "action должен быть от 1 до 5")
    val action: Int,

    val before: String? = null,

    val after: String? = null,

    val userName: String? = null,

    val userIp: String? = null,

    val programm: String? = "QShop API",

    val note: String? = null
)
