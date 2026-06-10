package com.example.oracleapi.dto.prcDoc.head

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class PrcdocheadInsRequest(

    @field:NotNull
    @field:Positive(message = "должно быть положительным")
    val crn: Long,

    @field:NotNull
    @field:Positive(message = "должно быть положительным")
    val doctype: Long,

    @field:NotNull
    @field:Size(min = 1, max = 10)
    val docpref: String,

    @field:Positive(message = "должно быть положительным или null")
    val docnumb: Long? = null,

    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @Schema(description = "Дата", example = "01.06.2026")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    val docdate: LocalDate? = null,

    @field:Positive(message = "должно быть положительным или null")
    val orderhead: Long? = null,

    val note: String? = null,

    @field:Positive(message = "должно быть положительным")
    val rn: Long? = null,

    @field:NotNull
    @field:Size(min = 6, max = 6)
    @field:Pattern(regexp = "^\\d{6}$", message = "должен содержать 6 цифр")
    val checkRoznPrice: String,

    @field:NotNull
    val isCheckOnly: Boolean = false,

    @field:NotNull
    val isUpdate: Boolean = false
)
