package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderhead.OrderHeadBasisDocUpdateRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import com.example.oracleapi.dto.orderhead.OrderHeadStatusResponse
import com.example.oracleapi.dto.orderhead.OrderHeadStatusUpdateRequest
import com.example.oracleapi.dto.orderhead.OrderHeadStatusUpdateResponse
import com.example.oracleapi.dto.orderhead.OrderHeadUpdRequest
import com.example.oracleapi.dto.orderhead.OrderHeadUpdResponse
import com.example.oracleapi.dto.orderspec.OrderSpecInsRequest
import com.example.oracleapi.dto.orderspec.OrderSpecInsResponse
import com.example.oracleapi.service.orderhead.OrderHeadService
import com.example.oracleapi.service.orderhead.OrderHeadStatusUpdate
import com.example.oracleapi.service.orderspec.OrderSpecService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/orderhead")
@Tag(name = "Заказ поставщику", description = "API для работы с заголовками заказов")
class OrderHeadController(
    private val orderHeadService: OrderHeadService,
    private val orderSpecService: OrderSpecService
) : BaseController() {
    @PostMapping("/ins_head")
    @Operation(summary = "Создание заголовка")
    fun createOrder(
        @Valid @RequestBody request: OrderHeadInsRequest
    ): MyApiResponse<OrderHeadInsResponse> {
        return success(orderHeadService.createOrder(request))
    }

    @PostMapping("/ins_spec")
    @Operation(summary = "Создание спецификации")
    fun createOrderSpec(
        @Valid @RequestBody request: OrderSpecInsRequest
    ): MyApiResponse<OrderSpecInsResponse> {
        return success(orderSpecService.createOrderSpec(request))
    }

    @PostMapping("/upd_header")
    @Operation(summary = "Обновлени заголовка")
    fun updateOrder(
        @Valid @RequestBody request: OrderHeadUpdRequest
    ): MyApiResponse<OrderHeadUpdResponse> {
        return success(orderHeadService.updateOrder(request))
    }

    @GetMapping("/status")
    @Operation(summary = "Получить статус документа")
    fun getStatus(
        @Valid rn: Long
    ): MyApiResponse<OrderHeadStatusResponse> {
        return success(orderHeadService.getStatus(rn))
    }

    @PostMapping("/update_status")
    @Operation(summary = "Установит статус документа")
    fun updateOrderStatus(
        @Valid @RequestBody request: OrderHeadStatusUpdateRequest
    ): MyApiResponse<OrderHeadStatusUpdateResponse> {
        return success(orderHeadService.updateStatus(request))
    }

    @PutMapping("/update_basis_doc")
    @Operation(summary = "Установить документ от поставщика")
    fun updateBasisDoc(
        @Valid @RequestBody request: OrderHeadBasisDocUpdateRequest
    ): MyApiResponse<ResponseRN> {
        return success(orderHeadService.updateBasisDoc(request))
    }

    @DeleteMapping("/delete_basis_doc")
    @Operation(summary = "Очистить документ от поставщика")
    fun clearBasisDocs(
        @Valid rn: Long
    ): MyApiResponse<ResponseRN> {
        return success(orderHeadService.clearBasisDocs(rn))
    }

}