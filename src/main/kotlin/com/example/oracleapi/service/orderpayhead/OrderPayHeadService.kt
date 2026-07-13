package com.example.oracleapi.service.orderpayhead

import com.example.oracleapi.dto.orderpay.OrderpayheadDto
import com.example.oracleapi.repository.orderpayhead.OrderpayheadRepository
import org.springframework.stereotype.Service

@Service
class OrderPayHeadService(
    private val orderpayheadRepository: OrderpayheadRepository
) {
    fun getOrderPayHeadByOrderhead(orderRn: Long): List<OrderpayheadDto> {
        return orderpayheadRepository.findOrderPayHeadByOrderhead(orderRn).map {
            OrderpayheadDto.fromOrderPayHead(it)
        }
    }

}
