package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.orderhead.note.OrderHeadUpdateNoteRequest
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import org.springframework.stereotype.Component

@Component
class OrderHeadUpdateNote(
    private val orderHeadRepository: OrderheadRepository
) {
    fun update(request: OrderHeadUpdateNoteRequest): RnResponse {
        val update = orderHeadRepository.updateNote(request.orderhead, request.note)
        if (update == 0) {
            throw IllegalArgumentException("Заказ с RN:${request.orderhead} не найден")
        }
        return RnResponse(
            request.orderhead ?: 0
        )
    }
}