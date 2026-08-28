package com.example.oracleapi.service.storeoper

import com.example.oracleapi.dto.storeoper.StoreoperDto
import org.springframework.stereotype.Service

/**
 * Сервис работы со складскими операциями
 * */
@Service
class StoreOperService(
    private val storeOperGet: StoreOperGet
) {
    /**
     * @return Список складских операций
     * */
    fun allRecords(): List<StoreoperDto> {
        return storeOperGet.getAll()
    }
}