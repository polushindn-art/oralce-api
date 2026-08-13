package com.example.oracleapi.service.tohead

import com.example.oracleapi.dto.tohead.ToheadDto
import org.springframework.stereotype.Service

@Service
class ToheadService(
    private val toheadFindRN: ToheadFindRN
) {
    fun toheadFindByRn(rn: Long): ToheadDto {
        return toheadFindRN.findToheadByRn(rn)
    }
}