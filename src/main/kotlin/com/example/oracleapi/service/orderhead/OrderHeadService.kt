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
        require(request.crn > 0) { "CRN обязателен" }
        require(request.doctype > 0) { "DOCTYPE обязателен" }
        require(request.docpref.isNotBlank()) { "DOCPREF обязателен" }
        require(request.docdate != null) { "DOCDATE обязательна" }
        require(request.storein > 0) { "STOREIN обязателен" }
        require(request.provider > 0) { "PROVIDER обязателен" }
        require(request.ul > 0) { "UL обязателен" }

        return orderHeadInsProcedure.execute(request)
    }
}