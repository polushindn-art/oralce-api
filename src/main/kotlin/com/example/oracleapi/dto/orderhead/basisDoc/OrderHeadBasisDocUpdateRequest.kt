package com.example.oracleapi.dto.orderhead.basisDoc

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class OrderHeadBasisDocUpdateRequest(
    val rn: Long,
    val type: Long,
    val pref: String,
    val number: Long,
    @JsonFormat(pattern = "dd.MM.yyyy")
    val date: LocalDate
)