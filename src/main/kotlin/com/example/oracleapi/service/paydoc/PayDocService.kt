package com.example.oracleapi.service.paydoc

import com.example.oracleapi.dto.paydoc.PaydocDto
import com.example.oracleapi.repository.paydoc.PaydocRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PayDocService(
    private val paydocRepository: PaydocRepository
) {
    @Transactional(readOnly = true)
    fun getByRn(rn: Long): List<PaydocDto> {
        return paydocRepository.findAllByRn(rn).map {
            PaydocDto.fromEntity(it)
        }
    }
}
