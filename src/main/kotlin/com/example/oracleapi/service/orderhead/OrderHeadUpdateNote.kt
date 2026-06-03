package com.example.oracleapi.service.orderhead

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderhead.note.OrderHeadUpdateNoteRequest
import com.example.oracleapi.repository.orderhead.OrderheadRepository
import org.springframework.stereotype.Component

@Component
class OrderHeadUpdateNote(
    private val orderHeadRepository: OrderheadRepository
) {
    fun update(request: OrderHeadUpdateNoteRequest): ResponseRN {
        val update = orderHeadRepository.updateNote(request.orderhead, request.note)
        if (update == 0) {
            throw IllegalArgumentException("Заказ с RN:${request.orderhead} не найден")
        }
        return ResponseRN(
            request.orderhead ?: 0
        )
    }
}