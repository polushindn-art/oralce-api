package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.repository.mark.CustomMarkRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarkViewService(
    private val markViewRepository: CustomMarkRepository
) {

    @Transactional(readOnly = true)
    fun findByKm(request: MarkFindRequest): MarkFindResponse {
        return markViewRepository.findByKm(request.km) ?: throw IllegalArgumentException("КМ не найден")
    }
}