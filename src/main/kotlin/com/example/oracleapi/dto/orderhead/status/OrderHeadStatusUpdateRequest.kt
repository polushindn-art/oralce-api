package com.example.oracleapi.dto.orderhead.status

import jakarta.validation.constraints.NotNull

data class OrderHeadStatusUpdateRequest(
    val rn: Long,
    @field:NotNull
    var status: Long? = null
)
