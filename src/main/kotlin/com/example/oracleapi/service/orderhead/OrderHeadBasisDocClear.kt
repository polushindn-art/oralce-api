package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class OrderHeadBasisDocClear(
    private val orderheadRepository: OrderheadRepository
) {
    @Transactional
    fun clearBasisDoc(rn: Long): RnResponse {
        require(rn > 0) { "RN обязательна" }

        val updated = orderheadRepository.clearBasisDoc(rn)
        if (updated == 0) {
            throw IllegalArgumentException("Заказ с RN=$rn не найден")
        }

        return RnResponse(rn)
    }
}