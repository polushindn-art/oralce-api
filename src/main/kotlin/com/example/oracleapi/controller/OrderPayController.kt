package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderpay.OrderpayheadDto
import com.example.oracleapi.service.orderpayhead.OrderPayHeadService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/orderpay")
@Tag(name = "Оплата поставщикам", description = "OrderPay")
class OrderPayController(
    private val orderPayHeadService: OrderPayHeadService
) : BaseController() {

    @GetMapping("/getHead/{orderRn}")
    fun getOrderPayHead(
        @PathVariable orderRn: Long,
    ): MyApiResponse<List<OrderpayheadDto>> {
        return successList(orderPayHeadService.getOrderPayHeadByOrderhead(orderRn))
    }

}