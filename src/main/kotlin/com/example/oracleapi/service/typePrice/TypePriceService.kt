package com.example.oracleapi.service.typePrice

import com.example.oracleapi.repository.typeprice.TypepriceRepository
import org.springframework.stereotype.Service

@Service
class TypePriceService(
    private val typePriceRepository: TypepriceRepository
) {
    fun existByRn(rn: Long): Boolean {
        return typePriceRepository.existsById(rn)
    }
}