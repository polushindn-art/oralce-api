package com.example.oracleapi.dto

import jakarta.validation.constraints.NotNull

data class RequestRN(
    @field:NotNull(message = "RN обязательна")
    var rn: Long? = null
)
