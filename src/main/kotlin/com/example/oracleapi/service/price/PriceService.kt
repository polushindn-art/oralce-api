package com.example.oracleapi.service.price

import com.example.oracleapi.dto.price.RequestPriceDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PriceService(
    private val getPriceOut: GetPriceOut
) {
    @Transactional(readOnly = true)
    fun getPriceOut(request: RequestPriceDto): Float {
        return getPriceOut.take(request)
    }
}