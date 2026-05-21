package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderhead.OrderHeadExamples
import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import com.example.oracleapi.service.orderhead.OrderHeadService

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.parameters.RequestBody

@RestController
@RequestMapping("/v1/orderhead")
@Tag(name = "Заказ поставщику", description = "API для работы с заголовками заказов")
class OrderHeadController(
    private val orderHeadService: OrderHeadService
) : BaseController() {
    @PostMapping("/create")
    @Operation(
        summary = "Создание заказа",
        description = "Выполняет процедуру PKG_ORDERHEAD.INS для создания или обновления заказа",
        requestBody = RequestBody(
            description = "Данные для создания заказа",
            required = true,
            content = [Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        name = "create",
                        summary = "Создание нового заказа",
                        value = OrderHeadExamples.FULL
                    )
                ]
            )]
        )
    )
    fun createOrder(
        @Valid @RequestBody request: OrderHeadInsRequest
    ): MyApiResponse<OrderHeadInsResponse> {
        return success(orderHeadService.createOrder(request))
    }
}