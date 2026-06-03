package com.example.oracleapi.dto.orderhead.status

import com.example.oracleapi.dto.field.FieldResponse

data class OrderHeadStatusUpdateResponse(
    val rn: Long? = null,
    val status: Long? = null,
    val field: FieldResponse? = null,
)