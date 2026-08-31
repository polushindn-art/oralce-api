package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.orderhead.*
import com.example.oracleapi.dto.orderhead.arrivalDate.OrderHeadUpdateArDateRequest
import com.example.oracleapi.dto.orderhead.arrivalDate.OrderHeadUpdateArDateResponse
import com.example.oracleapi.dto.orderhead.basisDoc.OrderHeadBasisDocUpdateRequest
import com.example.oracleapi.dto.orderhead.note.OrderHeadUpdateNoteRequest
import com.example.oracleapi.dto.orderhead.provider.OrderHeadProviderUpdateResponse
import com.example.oracleapi.dto.orderhead.provider.OrderHeadUpdateProviderRequest
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusUpdateRequest
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusUpdateResponse
import com.example.oracleapi.dto.orderhead.storein.OrderHeadUpdateStoreInRequest
import com.example.oracleapi.dto.orderhead.storein.OrderHeadUpdateStoreInResponse
import com.example.oracleapi.dto.orderhead.ul.OrderHeadUlUpdateRequest
import com.example.oracleapi.dto.orderhead.ul.OrderHeadUlUpdateResponse
import com.example.oracleapi.entity.table.Field
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import com.example.oracleapi.service.field.FieldService
import com.example.oracleapi.service.typedoc.TypedocService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderHeadService(
    private val orderHeadInsProcedure: OrderHeadInsProcedure,
    private val orderHeadUpdProcedure: OrderHeadUpdProcedure,
    private val orderHeadStatusGet: OrderHeadStatusGet,
    private val orderHeadStatusUpdate: OrderHeadStatusUpdate,
    private val orderHeadBasisDocClear: OrderHeadBasisDocClear,
    private val orderHeadBasisDocUpdate: OrderHeadBasisDocUpdate,
    private val orderHeadUpdateProvider: OrderHeadUpdateProvider,
    private val orderHeadUlUpdate: OrderHeadUlUpdate,
    private val orderHeadUpdateStoreIn: OrderHeadUpdateStoreIn,
    private val orderHeadUpdateArrivalDate: OrderHeadUpdateArrivalDate,
    private val orderHeadUpdateStoreInAndUl: OrderHeadUpdateStoreInAndUl,
    private val orderHeadUpdateNote: OrderHeadUpdateNote,
    private val fieldService: FieldService,
    private val typeDocService: TypedocService,
    private val orderheadRepository: OrderheadRepository
) {

    @Transactional
    fun createOrder(request: com.example.oracleapi.dto.orderhead.ins.OrderHeadInsRequest): com.example.oracleapi.dto.orderhead.ins.OrderHeadInsResponse {
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

    @Transactional
    fun updateOrder(request: OrderHeadUpdRequest): OrderHeadUpdResponse {
        request.rn?.let { require(it > 0) { "RN обязательна для обновления" } }
        return orderHeadUpdProcedure.execute(request)
    }

    @Transactional(readOnly = true)
    fun getStatus(rn: Long?): com.example.oracleapi.dto.orderhead.status.OrderHeadStatusResponse {
        rn?.let { require(rn > 0L) { "RN обязательна" } }
        return orderHeadStatusGet.take(rn ?: 0)
    }

    @Transactional
    fun updateStatus(request: OrderHeadStatusUpdateRequest): OrderHeadStatusUpdateResponse {
        require(request.rn > 0) { "RN обязательна" }
        require(request.status >= 0) { "Статус не может быть отрицательным" }
        fieldService.validateFieldValue(Field.ORDER_STATUS, request.status)

        return orderHeadStatusUpdate.take(request)
    }

    @Transactional
    fun updateBasisDoc(request: OrderHeadBasisDocUpdateRequest): RnResponse {
        require(request.rn > 0) { "RN обязательна" }
        typeDocService.validateExists(request.type)
        return orderHeadBasisDocUpdate.update(request)
    }

    @Transactional
    fun clearBasisDocs(rn: Long): RnResponse {
        require(rn > 0) { "RN обязательна" }
        return orderHeadBasisDocClear.clearBasisDoc(rn)
    }

    @Transactional
    fun updateProvider(request: OrderHeadUpdateProviderRequest): OrderHeadProviderUpdateResponse {
        require(request.orderhead > 0) { "RN заказа обязательна" }
        require(request.provider > 0) { "Поставщик обязателен" }
        return orderHeadUpdateProvider.update(request)
    }

    @Transactional
    fun updateUl(request: OrderHeadUlUpdateRequest): OrderHeadUlUpdateResponse {
        require(request.orderhead > 0) { "RN заказа обязательна" }
        require(request.ul > 0) { "Наше юридическое лицо обязательно" }
        return orderHeadUlUpdate.update(request)
    }

    @Transactional
    fun updateStoreIn(request: OrderHeadUpdateStoreInRequest): OrderHeadUpdateStoreInResponse {
        request.orderhead?.let { require(it > 0) { "RN заказа обязательна" } }
        request.storeIn?.let { require(it > 0) { "Склад обязателен" } }
        return orderHeadUpdateStoreIn.update(request)
    }

    @Transactional
    fun updateArDate(request: OrderHeadUpdateArDateRequest): OrderHeadUpdateArDateResponse {
        request.orderhead?.let { require(it > 0) { "RN заказа обязательна" } }
        require(request.arrivaldate != null) { "Дата обязательна" }
        //require(request.arrivaldate.isAfter(LocalDate.now())) { "Дата прибытия должна быть в будущем" }
        return orderHeadUpdateArrivalDate.update(request)
    }

    @Transactional
    fun updateNote(request: OrderHeadUpdateNoteRequest): RnResponse {
        request.orderhead?.let { require(it > 0) { "RN заказа обязательна" } }
        request.note?.let { require(it.isNotBlank()) { "Отсутствует текст примечания" } }
        return orderHeadUpdateNote.update(request)
    }

    @Transactional
    fun updateStoreInAndUl(request: OrderHeadUpdStoreInAndUlRequest): OrderHeadUpdStoreInAndUlResponse {
        request.orderhead?.let { require(it > 0) { "RN заказа обязательна" } }
        request.storeIn?.let { require(it > 0) { "Склад обязателен" } }
        request.ul?.let { require(it > 0) { "Наше ЮЛ обязательно" } }
        return orderHeadUpdateStoreInAndUl.update(request)
    }

    @Transactional(readOnly = true)
    fun existByRn(rn: Long): Boolean {
        return orderheadRepository.existsByRn(rn)
    }

}