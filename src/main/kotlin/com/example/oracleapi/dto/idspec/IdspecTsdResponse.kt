package com.example.oracleapi.dto.idspec

import java.math.BigDecimal

data class IdspecTsdResponse(
    val rn: Long,
    val nomenId: Long,
    val nomenCode: String?,
    val article: String?,
    val nomenName: String?,
    val quant: BigDecimal?,
    val summ: BigDecimal?,
    val inprice: BigDecimal?,
    val storein: Long?,
    val storeout: Long?,
    val km: List<String>? = emptyList()
)