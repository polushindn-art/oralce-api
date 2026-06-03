package com.example.oracleapi.dto.orderhead.storein

import com.example.oracleapi.dto.store.StoreResponse

data class OrderHeadUpdateStoreInResponse(
    val orderhead: Long? = null,
    val updateCountSpec: Int,
    val storeIn: StoreResponse
)
