package com.example.oracleapi.service.acatalog

import com.example.oracleapi.dto.RnResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AcatalogService(
    private val getCatalogByNomen: GetCatalogByNomen
) {

    @Transactional(readOnly = true)
    fun getCatalogByNomenForOrder(nomen: Long): RnResponse {
        return getCatalogByNomen.take(nomen)
    }

}