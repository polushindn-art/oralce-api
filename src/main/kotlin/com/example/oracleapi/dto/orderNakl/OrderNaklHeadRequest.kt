package com.example.oracleapi.dto.orderNakl

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate


data class OrderNaklHeadRequest(
    @field:NotNull
    @field:Positive(message = "должно быть положительным")
    val prn: Long,

    @field:NotNull
    @field:Positive(message = "должно быть положительным")
    val provider: Long,

    @field:Positive(message = "должно быть положительным")
    val basisdoctype: Long ?= null,

    @field:Size(min = 1, max = 10)
    val basisdocpref: String? = null,

    @field:Positive(message = "должно быть положительным или null")
    val basisdocnumb: Long? = null,

    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Дата", example = "01.06.2026")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    val basisdocdate: LocalDate? = null,

    val numbttn: Long? = null,

    @field:NotNull
    val isUpdate: Boolean = false

)