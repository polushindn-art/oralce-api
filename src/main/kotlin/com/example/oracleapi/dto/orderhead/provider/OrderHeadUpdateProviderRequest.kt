package com.example.oracleapi.dto.orderhead.provider

data class OrderHeadUpdateProviderRequest(
    val orderhead: Long,
    val provider: Long
)