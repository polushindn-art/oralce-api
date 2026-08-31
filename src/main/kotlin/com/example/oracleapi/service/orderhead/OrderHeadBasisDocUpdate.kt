package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.orderhead.basisDoc.OrderHeadBasisDocUpdateRequest
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class OrderHeadBasisDocUpdate(
    private val orderheadRepository: OrderheadRepository
) {
    @Transactional
    fun update(request: OrderHeadBasisDocUpdateRequest): RnResponse {
        val updated = orderheadRepository.updateBasisdoc(
            request.rn,
            request.type,
            request.pref,
            request.number,
            request.date
        )
        if (updated == 0) {
            throw IllegalArgumentException("OrderHead с RN=${request.rn} не найден")
        }
        return RnResponse(request.rn)
    }

}