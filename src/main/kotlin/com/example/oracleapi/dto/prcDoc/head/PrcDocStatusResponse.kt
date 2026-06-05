package com.example.oracleapi.dto.prcDoc.head

import com.example.oracleapi.dto.field.FieldResponse

data class PrcDocStatusResponse(
    val rn: Long,
    val status: Long? = null,
    val field: FieldResponse? = null,
    val cur: MutableList<Map<String, Any?>>?
)
