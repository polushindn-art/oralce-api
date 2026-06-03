package com.example.oracleapi.dto.orderhead.ul

import com.example.oracleapi.dto.agn.AgnListResponse

data class OrderHeadUlUpdateResponse(
    val orderhead: Long,
    val ul: AgnListResponse
)