package com.example.oracleapi.dto.orderhead

import com.example.oracleapi.dto.store.StoreResponse

data class OrderHeadUpdStoreInAndUlResponse(
    val orderhead: Long? = null,
    val updateCountSpec: Int,
)