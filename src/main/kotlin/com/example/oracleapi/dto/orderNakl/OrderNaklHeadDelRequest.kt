package com.example.oracleapi.dto.orderNakl

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*

data class OrderNaklHeadDelRequest(

    @field:Positive(message = "должно быть больше 0")
    @field:Schema(
        description = "RN документа для удаления",
        minimum = "1",
        required = true
    )
    val rn: Long,

    @field:Schema(
        required = false,
        example = "false"
    )
    val isUpdate:Boolean? = false
)
