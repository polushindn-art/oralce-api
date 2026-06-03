package com.example.oracleapi.dto.orderhead.arrivalDate

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class OrderHeadUpdateArDateRequest(
    @Schema(description = "Заголоваок")
    val orderhead: Long? = null,
    @Schema(description = "Дата прихзода")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    val arrivaldate: LocalDate? = null,
)
