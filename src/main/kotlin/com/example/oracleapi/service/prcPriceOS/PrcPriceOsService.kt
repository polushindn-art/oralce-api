package com.example.oracleapi.service.prcPriceOS

import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.prcPriceOS.PrcPriceOSRequest
import org.springframework.stereotype.Service

@Service
class PrcPriceOsService(
    private val prcPriceOsIns: PrcPriceOsIns
) {
    fun ins(request: PrcPriceOSRequest): RnResponse {
        return prcPriceOsIns.take(request)
    }
}