package com.example.oracleapi.dto.prcDoc.head

data class PrcdocheadInsResponse(
    val rn: Long,
    val docnumb: Long,
    val cur: MutableList<Map<String, Any?>>?
)