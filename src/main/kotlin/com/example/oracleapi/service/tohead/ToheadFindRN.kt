package com.example.oracleapi.service.tohead

import com.example.oracleapi.dto.tohead.ToheadDto
import com.example.oracleapi.repository.tohead.ToheadRepository
import org.springframework.stereotype.Component

@Component
class ToheadFindRN(
    private val toheadRepository: ToheadRepository
) {
    fun findToheadByRn(rn: Long): ToheadDto {
        return toheadRepository.findToheadByRn(rn).let { ToheadDto.fromEntity(it) }
    }
}