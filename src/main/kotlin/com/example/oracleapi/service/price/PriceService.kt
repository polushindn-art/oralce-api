package com.example.oracleapi.service.price

import com.example.oracleapi.dto.price.RequestPriceDto
import org.springframework.stereotype.Service

@Service
class PriceService(
    private val getPriceOut: GetPriceOut
) {
    fun getPriceOut(request: RequestPriceDto): Float {
        return getPriceOut.take(request)
    }
}