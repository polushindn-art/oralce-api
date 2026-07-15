package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderpay.OrderDepositRequest
import com.example.oracleapi.dto.orderpay.OrderDepositResponse
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertResponse
import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertResponse
import com.example.oracleapi.dto.orderpay.OrderpayheadDto
import com.example.oracleapi.service.orderpayhead.OrderPayHeadService
import com.example.oracleapi.service.orderpayspec.OrderPaySpecService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/orderpay")
@Tag(name = "Оплата поставщикам", description = "OrderPay")
class OrderPayController(
    private val orderPayHeadService: OrderPayHeadService,
    private val orderPaySpecService: OrderPaySpecService
) : BaseController() {

    @GetMapping("/getHead/{orderRn}")
    @Operation(description = "orderpayhead + orderpayspec", summary = "Документы оплаты")
    fun getOrderPayHead(
        @PathVariable orderRn: Long,
    ): MyApiResponse<List<OrderpayheadDto>> {
        return successList(orderPayHeadService.getOrderPayHeadByOrderhead(orderRn))
    }

    @PostMapping("/getOrderDeposit")
    @Operation(description = "get_order_deposit", summary = "Функция расчета вносимых средств")
    fun getOrderDeposit(
        @Valid @RequestBody request: OrderDepositRequest
    ): MyApiResponse<List<OrderDepositResponse>> {
        return successList(orderPayHeadService.getOrderDeposit(request))
    }

    @PostMapping("/ins_head")
    @Operation(description = "ins_head", summary = "Создать заголовок")
    fun insHead(
        @Valid @RequestBody request: OrderPayHeadInsertRequest
    ): MyApiResponse<OrderPayHeadInsertResponse> {
        return success(orderPayHeadService.insertHead(request))
    }

    @PostMapping("/ins_spec")
    @Operation(description = "ins_spec", summary = "Создать спецификацию")
    fun insSpec(
        @Valid @RequestBody request: OrderPaySpecInsertRequest
    ): MyApiResponse<OrderPaySpecInsertResponse> {
        return success(orderPaySpecService.insertSpec(request))
    }

}