package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.orderhead.OrderHeadUpdStoreInAndUlRequest
import com.example.oracleapi.dto.orderhead.OrderHeadUpdStoreInAndUlResponse
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import com.example.oracleapi.repository.orderspec.OrderspecRepository
import com.example.oracleapi.service.StoreService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderHeadUpdateStoreInAndUl(
    private val orderHeadRepository: OrderheadRepository,
    private val orderSpecRepository: OrderspecRepository,
    private val storeService: StoreService
) {
    @Transactional
    fun update(request: OrderHeadUpdStoreInAndUlRequest): OrderHeadUpdStoreInAndUlResponse {
        val store = storeService.getStoreByRn(request.storeIn)

        val updateHead = orderHeadRepository.updateStoreinAndUl(request.orderhead, request.storeIn, request.ul)
        if (updateHead == 0) {
            throw IllegalArgumentException("Заказ с RN: ${request.orderhead} не найден")
        }

        val updateSpec = orderSpecRepository.updateStoreIn(request.orderhead, request.storeIn)

        return OrderHeadUpdStoreInAndUlResponse(
            request.orderhead,
            updateSpec
        )

    }
}