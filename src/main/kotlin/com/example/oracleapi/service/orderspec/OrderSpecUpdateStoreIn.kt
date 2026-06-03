package com.example.oracleapi.service.orderspec

import com.example.oracleapi.repository.orderspec.OrderspecRepository
import org.springframework.stereotype.Component

@Component
class OrderSpecUpdateStoreIn(
    private val orderspecRepository: OrderspecRepository
) {

    fun update(prn: Long, store: Long): Boolean {
        return (orderspecRepository.updateStoreIn(prn, store) == 1)
    }

}