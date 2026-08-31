package com.example.oracleapi.service.orderpayspec

import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderPaySpecService(
    private val orderPaySpecIns: OrderPaySpecIns
) {
    @Transactional
    fun insertSpec(request: OrderPaySpecInsertRequest): OrderPaySpecInsertResponse {
        return orderPaySpecIns.take(request)
    }
}