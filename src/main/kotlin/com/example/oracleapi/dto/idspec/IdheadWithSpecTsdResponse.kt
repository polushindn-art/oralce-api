package com.example.oracleapi.dto.idspec

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDateTime

data class IdheadWithSpecTsdResponse(
    val rn: Long,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val docdate: LocalDateTime?,
    val docnumb: BigDecimal?,
    val docpref: String?,
    val idStatus: Long,
    val idStatusCode: String?,
    val provider: Long?,
    val storeinCode: String?,
    val storeoutCode: String?,
    val note: String?,
    val sumprice: BigDecimal?,
    val specs: List<IdspecTsdResponse>
)