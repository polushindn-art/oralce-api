package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import com.example.oracleapi.service.orderhead.OrderHeadService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/orderhead")
@Tag(name = "Заказ поставщику", description = "API для работы с заголовками заказов")
class OrderHeadController(
    private val orderHeadService: OrderHeadService
) : BaseController() {
    @PostMapping("/create")
    fun createOrder(
        @Valid @RequestBody request: OrderHeadInsRequest
    ): MyApiResponse<OrderHeadInsResponse> {
        println("Received request: $request")
        println("crn = ${request.crn}")
        return success(orderHeadService.createOrder(request))
    }

    @PostMapping("/test")
    fun test(@RequestBody body: String?): String {  // ← получаем как строку
        println("Raw body: $body")
        return body ?: "No body"
    }
}