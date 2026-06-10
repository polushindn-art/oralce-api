package com.example.oracleapi.dto.orderspec.del

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*


data class OrderSpecDelRequest(

    @field:Positive(message = "RN больше 0")
    @field:Schema(
        description = "RN спецификации",
        required = true,
    )
    val rn: Long,

    @field:Schema(
        required = false
    )
    val isUpdate: Boolean = false,

    @field:Schema(
        required = false
    )
    val changeOverHead: Int = 0,

    @field:Schema(
        required = false
    )
    val isSplit: Boolean = false
)
