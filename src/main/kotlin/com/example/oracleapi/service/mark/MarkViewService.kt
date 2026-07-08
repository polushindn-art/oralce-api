package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.repository.mark.CustomMarkRepository
import org.springframework.stereotype.Service

@Service
class MarkViewService(
    private val markViewRepository: CustomMarkRepository
) {
    fun findByKm(request: MarkFindRequest): MarkFindResponse {
        return markViewRepository.findByKm(request.km) ?: throw IllegalArgumentException("КМ не найден")
    }
}