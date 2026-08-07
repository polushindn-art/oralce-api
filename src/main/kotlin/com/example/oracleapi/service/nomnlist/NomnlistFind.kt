package com.example.oracleapi.service.nomnlist

import com.example.oracleapi.dto.nomnlist.NomnlistDto
import com.example.oracleapi.repository.nomnlist.NomnlistRepository
import org.springframework.stereotype.Component

@Component
class NomnlistFind(
    private val nomnlistRepository: NomnlistRepository
) {
    fun find(rn: Long): NomnlistDto? {
        val entity = nomnlistRepository.findByRn(rn)
        return entity?.let { NomnlistDto.fromEntity(it) }
    }
}