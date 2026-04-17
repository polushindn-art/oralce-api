package com.example.oracleapi.dto.idspec

import com.example.oracleapi.dto.idspec.IdspecTsdResponse
import java.math.BigDecimal
import java.time.LocalDateTime

data class IdheadWithSpecTsdResponse(
    val rn: Long,
    val docdate: LocalDateTime?,
    val docnumb: BigDecimal?,
    val docpref: String?,
    val idStatus: Long,
    val provider: Long?,
    val storein: Long?,
    val storeout: Long?,
    val note: String?,
    val sumprice: BigDecimal?,
    val specs: List<IdspecTsdResponse>
)