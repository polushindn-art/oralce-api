package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.orderhead.storein.OrderHeadUpdateStoreInRequest
import com.example.oracleapi.dto.orderhead.storein.OrderHeadUpdateStoreInResponse
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import com.example.oracleapi.repository.orderspec.OrderspecRepository
import com.example.oracleapi.service.StoreService
import com.example.oracleapi.service.orderspec.OrderSpecService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderHeadUpdateStoreIn(
    private val orderHeadRepository: OrderheadRepository,
    private val orderSpecRepository: OrderspecRepository,
    private val storeService: StoreService
) {
    @Transactional
    fun update(request: OrderHeadUpdateStoreInRequest): OrderHeadUpdateStoreInResponse{

        val store = storeService.getStoreByRn(request.storeIn)

        val updateHead = orderHeadRepository.updateStorein(
            request.orderhead,
            request.storeIn
        )
        if (updateHead == 0) {
            throw IllegalArgumentException("Заказ с RN: ${request.orderhead} не найден")
        }

        val updateSpec = orderSpecRepository.updateStoreIn(request.orderhead, request.storeIn)

        return OrderHeadUpdateStoreInResponse(
            request.orderhead,
            updateSpec,
            store
        )

    }

}