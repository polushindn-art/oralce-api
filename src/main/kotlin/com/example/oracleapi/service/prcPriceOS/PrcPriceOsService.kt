package com.example.oracleapi.service.prcPriceOS

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.prcPriceOS.PrcPriceOSRequest
import org.springframework.stereotype.Service

@Service
class PrcPriceOsService(
    private val prcPriceOsIns: PrcPriceOsIns
) {
    fun ins(request: PrcPriceOSRequest): ResponseRN {
        return prcPriceOsIns.take(request)
    }
}