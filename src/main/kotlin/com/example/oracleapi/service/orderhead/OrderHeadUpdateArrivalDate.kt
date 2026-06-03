package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.orderhead.arrivalDate.OrderHeadUpdateArDateRequest
import com.example.oracleapi.dto.orderhead.arrivalDate.OrderHeadUpdateArDateResponse
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import org.springframework.stereotype.Component

@Component
class OrderHeadUpdateArrivalDate(
    private val orderheadRepository: OrderheadRepository
) {

    fun update(request: OrderHeadUpdateArDateRequest): OrderHeadUpdateArDateResponse {
        val update = orderheadRepository.updateArrivalDate(request.orderhead, request.arrivaldate)
        if (update == 0) {
            throw IllegalArgumentException("Заказ с RN:${request.orderhead} не найден")
        }
        return OrderHeadUpdateArDateResponse(
            request.orderhead,
            request.arrivaldate
        )
    }

}