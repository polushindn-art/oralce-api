package com.example.oracleapi.dto.orderhead.arrivalDate

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class OrderHeadUpdateArDateResponse(
    val orderhead: Long? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    val arrivaldate: LocalDate? = null
)