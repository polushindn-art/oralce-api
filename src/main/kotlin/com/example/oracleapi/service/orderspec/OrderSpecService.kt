package com.example.oracleapi.service.orderspec

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderspec.OrderSpecResponse
import com.example.oracleapi.dto.orderspec.del.OrderSpecDelRequest
import com.example.oracleapi.dto.orderspec.ins.OrderSpecInsRequest
import com.example.oracleapi.dto.orderspec.ins.OrderSpecInsResponse
import com.example.oracleapi.dto.orderspec.upd.OrderSpecUpdateRequest
import com.example.oracleapi.dto.orderspec.upd.OrderSpecUpdateResponse
import com.example.oracleapi.repository.orderspec.OrderspecRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderSpecService(
    private val orderSpecInsProcedure: OrderSpecInsProcedure,
    private val orderspecRepository: OrderspecRepository,
    private val orderSpecUpd: OrderSpecUpd,
    private val orderSpecDel: OrderSpecDel
) {
    fun createOrderSpec(request: OrderSpecInsRequest): OrderSpecInsResponse {
        require(request.prn != null && request.prn > 0) { "PRN обязателен" }
        require(request.nomen != null && request.nomen > 0) { "NOMEN обязателен" }
        require(request.quant != null) { "QUANT обязателен" }
        require(request.summ != null) { "SUMM обязателен" }
        require(request.prquant != null) { "PRQUANT обязателен" }

        return orderSpecInsProcedure.ins(request)
    }

    fun updateOrderSpec(request: OrderSpecUpdateRequest): OrderSpecUpdateResponse {
        require(request.rn > 0) { "RN обязателен" }
        return orderSpecUpd.take(request)
    }

    @Transactional(readOnly = true)
    fun getByRn(rn: Long): OrderSpecResponse {
        val orderSpec =
            orderspecRepository.findByRn(rn) ?: throw IllegalArgumentException("OrderSpec с RN=$rn не найден")
        return OrderSpecResponse.fromEntity(orderSpec)
    }

    fun del(request: OrderSpecDelRequest): ResponseRN {
        return orderSpecDel.delSpec(request)
    }

}