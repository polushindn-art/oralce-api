package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderhead.OrderHeadBasisDocUpdateRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import com.example.oracleapi.dto.orderhead.OrderHeadStatusResponse
import com.example.oracleapi.dto.orderhead.OrderHeadStatusUpdateRequest
import com.example.oracleapi.dto.orderhead.OrderHeadStatusUpdateResponse
import com.example.oracleapi.dto.orderhead.OrderHeadUpdRequest
import com.example.oracleapi.dto.orderhead.OrderHeadUpdResponse
import com.example.oracleapi.entity.Field
import com.example.oracleapi.service.field.FieldService
import com.example.oracleapi.service.typedoc.TypedocService
import org.springframework.stereotype.Service

@Service
class OrderHeadService(
    private val orderHeadInsProcedure: OrderHeadInsProcedure,
    private val orderHeadUpdProcedure: OrderHeadUpdProcedure,
    private val orderHeadStatusGet: OrderHeadStatusGet,
    private val orderHeadStatusUpdate: OrderHeadStatusUpdate,
    private val orderHeadBasisDocClear: OrderHeadBasisDocClear,
    private val orderHeadBasisDocUpdate: OrderHeadBasisDocUpdate,
    private val fieldService: FieldService,
    private val typeDocService: TypedocService
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

    fun updateOrder(request: OrderHeadUpdRequest): OrderHeadUpdResponse {
        request.rn?.let { require(it > 0) { "RN обязательна для обновления" } }
        return orderHeadUpdProcedure.execute(request)
    }

    fun getStatus(rn: Long?): OrderHeadStatusResponse {
        rn?.let { require(rn > 0L) { "RN обязательна" } }
        return orderHeadStatusGet.take(rn ?: 0)
    }

    fun updateStatus(request: OrderHeadStatusUpdateRequest): OrderHeadStatusUpdateResponse {
        require(request.rn > 0) { "RN обязательна" }
        require(request.status >= 0) { "Статус не может быть отрицательным" }
        fieldService.validateFieldValue(Field.ORDER_STATUS, request.status)

        return orderHeadStatusUpdate.take(request)
    }

    fun updateBasisDoc(request: OrderHeadBasisDocUpdateRequest): ResponseRN {
        require(request.rn > 0) { "RN обязательна" }
        typeDocService.validateExists(request.type)
        return orderHeadBasisDocUpdate.update(request)
    }

    fun clearBasisDocs(rn: Long): ResponseRN {
        require(rn > 0) { "RN обязательна" }
        return orderHeadBasisDocClear.clearBasisDoc(rn)
    }


}