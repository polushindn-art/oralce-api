package com.example.oracleapi.service.orderspec

import com.example.oracleapi.dto.orderspec.OrderSpecInsRequest
import com.example.oracleapi.dto.orderspec.OrderSpecInsResponse
import org.springframework.stereotype.Service

@Service
class OrderSpecService(
    private val orderSpecInsProcedure: OrderSpecInsProcedure
) {
    fun createOrderSpec(request: OrderSpecInsRequest): OrderSpecInsResponse {
        require(request.prn != null && request.prn > 0) { "PRN обязателен" }
        require(request.nomen != null && request.nomen > 0) { "NOMEN обязателен" }
        require(request.quant != null) { "QUANT обязателен" }
        require(request.summ != null) { "SUMM обязателен" }
        require(request.prquant != null) { "PRQUANT обязателен" }

        return orderSpecInsProcedure.ins(request)
    }
}