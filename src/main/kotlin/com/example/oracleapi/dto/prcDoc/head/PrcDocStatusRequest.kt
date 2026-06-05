package com.example.oracleapi.dto.prcDoc.head

import jakarta.validation.constraints.*


data class PrcDocStatusRequest(
    @field:Positive(message = "должно быть больше 0")
    val rn: Long,

    @field:PositiveOrZero(message = "должно быть больше или равно 0")
    @field:Min(0)
    @field:Max(6)
    val status: Long
)
