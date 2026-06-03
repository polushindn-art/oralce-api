package com.example.oracleapi.dto.orderhead.status

import com.example.oracleapi.dto.field.FieldResponse

data class OrderHeadStatusResponse(
    val rn : Long,
    val status: Long,
    val field: FieldResponse
)