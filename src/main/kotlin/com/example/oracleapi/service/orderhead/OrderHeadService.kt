package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import org.springframework.stereotype.Service

@Service
class OrderHeadService(
    private val orderHeadInsProcedure: OrderHeadInsProcedure
) {
    fun createOrder(request: OrderHeadInsRequest): OrderHeadInsResponse {
        // Валидация обязательных полей
        request.crn?.let { require(it > 0) { "CRN обязателен" } }
        request.doctype?.let { require(it > 0) { "DOCTYPE обязателен" } }
        require(request.docpref?.isNotBlank() == true) { "DOCPREF обязателен" }
        require(request.docdate != null) { "DOCDATE обязательна" }
        request.storein?.let { require(it > 0) { "STOREIN обязателен" } }
        request.provider?.let { require(it > 0) { "PROVIDER обязателен" } }
        request.ul?.let { require(it > 0) { "UL обязателен" } }

        return orderHeadInsProcedure.execute(request)
    }
}