package com.example.oracleapi.dto.orderNakl

import com.example.oracleapi.annotation.BindingDateFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDate


data class OrderNaklHeadRequest(
    @field:Positive(message = "должно быть положительным")
    val prn: Long,

    @field:Positive(message = "должно быть положительным")
    val provider: Long,

    @field:Positive(message = "должно быть положительным")
    val basisdoctype: Long ?= null,

    @field:Size(min = 1, max = 10)
    val basisdocpref: String? = null,

    @field:Positive(message = "должно быть положительным или null")
    val basisdocnumb: Long? = null,

    @field:Schema(description = "Дата", example = "01.06.2026")
    @field:BindingDateFormat
    val basisdocdate: LocalDate? = null,

    val numbttn: Long? = null,

    val isUpdate: Boolean = false

)