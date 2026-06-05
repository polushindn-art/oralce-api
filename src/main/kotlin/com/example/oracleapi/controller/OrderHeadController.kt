package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderhead.arrivalDate.OrderHeadUpdateArDateRequest
import com.example.oracleapi.dto.orderhead.arrivalDate.OrderHeadUpdateArDateResponse
import com.example.oracleapi.dto.orderhead.basisDoc.OrderHeadBasisDocUpdateRequest
import com.example.oracleapi.dto.orderhead.ins.OrderHeadInsResponse
import com.example.oracleapi.dto.orderhead.note.OrderHeadUpdateNoteRequest
import com.example.oracleapi.dto.orderhead.provider.OrderHeadProviderUpdateResponse
import com.example.oracleapi.dto.orderhead.provider.OrderHeadUpdateProviderRequest
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusResponse
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusUpdateRequest
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusUpdateResponse
import com.example.oracleapi.dto.orderhead.storein.OrderHeadUpdateStoreInRequest
import com.example.oracleapi.dto.orderhead.storein.OrderHeadUpdateStoreInResponse
import com.example.oracleapi.dto.orderhead.ul.OrderHeadUlUpdateRequest
import com.example.oracleapi.dto.orderhead.ul.OrderHeadUlUpdateResponse
import com.example.oracleapi.dto.orderspec.OrderSpecResponse
import com.example.oracleapi.dto.orderspec.ins.OrderSpecInsRequest
import com.example.oracleapi.dto.orderspec.ins.OrderSpecInsResponse
import com.example.oracleapi.dto.orderspec.upd.OrderSpecUpdateRequest
import com.example.oracleapi.dto.orderspec.upd.OrderSpecUpdateResponse
import com.example.oracleapi.service.orderhead.OrderHeadService
import com.example.oracleapi.service.orderspec.OrderSpecService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

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
        @Valid @RequestBody request: com.example.oracleapi.dto.orderhead.ins.OrderHeadInsRequest
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

    @PutMapping("/update_provider")
    @Operation(summary = "Обновить поставщика")
    fun updateProvider(
        @Valid @RequestBody request: OrderHeadUpdateProviderRequest
    ): MyApiResponse<OrderHeadProviderUpdateResponse> {
        return success(orderHeadService.updateProvider(request))
    }

    @PutMapping("/update_ul")
    @Operation(summary = "Обновить наше ЮЛ")
    fun updateUl(
        @Valid @RequestBody request: OrderHeadUlUpdateRequest
    ): MyApiResponse<OrderHeadUlUpdateResponse> {
        return success(orderHeadService.updateUl(request))
    }

    @PutMapping("/update_store_in")
    @Operation(summary = "Обновить склад поступления")
    fun updateStoreIn(@Valid @RequestBody request: OrderHeadUpdateStoreInRequest): MyApiResponse<OrderHeadUpdateStoreInResponse> {
        return success(orderHeadService.updateStoreIn(request))
    }

    @GetMapping("/orderspec")
    @Operation(summary = "Получить специтфикацию документа")
    fun getOrderSpec(
        @Valid rn: Long
    ): MyApiResponse<OrderSpecResponse> {
        return success(orderSpecService.getByRn(rn))
    }

    @PutMapping("/update_arrivaldate")
    @Operation(summary = "Обновить дату прихода")
    fun updateArrivalDate(
        @Valid @RequestBody request: OrderHeadUpdateArDateRequest
    ): MyApiResponse<OrderHeadUpdateArDateResponse> {
        return success(orderHeadService.updateArDate(request))
    }

    @PutMapping("/update_orderspec")
    @Operation(summary = "Обновить спецификацию документа")
    fun updateOrderSpec(
        @Valid @RequestBody request: OrderSpecUpdateRequest
    ): MyApiResponse<OrderSpecUpdateResponse> {
        return success(orderSpecService.updateOrderSpec(request))
    }

    @PutMapping("update_note")
    @Operation(summary = "Обновить примечание")
    fun updateNote(
        @Valid @RequestBody request: OrderHeadUpdateNoteRequest
    ): MyApiResponse<ResponseRN> {
        return success(orderHeadService.updateNote(request))
    }

}