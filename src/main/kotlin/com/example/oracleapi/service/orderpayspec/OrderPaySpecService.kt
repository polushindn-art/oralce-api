package com.example.oracleapi.service.orderpayspec

import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertResponse
import org.springframework.stereotype.Service

@Service
class OrderPaySpecService(
    private val orderPaySpecIns: OrderPaySpecIns
) {
    fun insertSpec(request: OrderPaySpecInsertRequest): OrderPaySpecInsertResponse {
        return orderPaySpecIns.take(request)
    }
}