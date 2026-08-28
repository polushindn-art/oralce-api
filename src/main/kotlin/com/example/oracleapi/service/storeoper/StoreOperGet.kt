package com.example.oracleapi.service.storeoper

import com.example.oracleapi.dto.storeoper.StoreoperDto
import com.example.oracleapi.repository.storeOper.StoreoperRepository
import org.springframework.stereotype.Component

@Component
class StoreOperGet(
    private val storeOperRepository: StoreoperRepository
) {
    fun getAll(): List<StoreoperDto> {
        return storeOperRepository.findAll().map { StoreoperDto.fromEntity(it) }
    }
}