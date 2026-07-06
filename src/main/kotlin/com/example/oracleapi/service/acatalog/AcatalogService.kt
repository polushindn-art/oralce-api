package com.example.oracleapi.service.acatalog

import com.example.oracleapi.dto.ResponseRN
import org.springframework.stereotype.Service

@Service
class AcatalogService(
    private val getCatalogByNomen: GetCatalogByNomen
) {

    fun getCatalogByNomenForOrder(nomen: Long): ResponseRN {
        return getCatalogByNomen.take(nomen)
    }

}