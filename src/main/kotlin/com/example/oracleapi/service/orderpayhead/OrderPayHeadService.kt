package com.example.oracleapi.service.orderpayhead

import com.example.oracleapi.dto.orderpay.OrderDepositRequest
import com.example.oracleapi.dto.orderpay.OrderDepositResponse
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertResponse
import com.example.oracleapi.dto.orderpay.OrderpayheadDto
import com.example.oracleapi.repository.orderpayhead.OrderDepositRepository
import com.example.oracleapi.repository.orderpayhead.OrderpayheadRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderPayHeadService(
    private val orderpayheadRepository: OrderpayheadRepository,
    private val orderDepositRepository: OrderDepositRepository,
    private val orderPayHeadIns: OrderPayHeadIns
) {

    @Transactional(readOnly = true)
    fun getOrderPayHeadByOrderhead(orderRn: Long): List<OrderpayheadDto> {
        return orderpayheadRepository.findOrderPayHeadByOrderhead(orderRn).map {
            OrderpayheadDto.fromOrderPayHead(it)
        }
    }

    @Transactional(readOnly = true)
    fun getOrderDeposit(request: OrderDepositRequest): List<OrderDepositResponse> {
        return orderDepositRepository.getOrderDeposit(request.orderRn, request.deposit)
    }

    @Transactional(readOnly = true)
    fun insertHead(request: OrderPayHeadInsertRequest): OrderPayHeadInsertResponse {
        return orderPayHeadIns.take(request)
    }

}
