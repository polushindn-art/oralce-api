package com.example.oracleapi.service.nomnlist

import com.example.oracleapi.repository.nomnlist.NomnlistRepository
import org.springframework.stereotype.Service

@Service
class NomnlistService(
    private val nomnlistRepository: NomnlistRepository
) {
    fun existsByRn(rn: Long): Boolean {
        return nomnlistRepository.existsByRn(rn)
    }
}
