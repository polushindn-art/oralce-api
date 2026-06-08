package com.example.oracleapi.service.ordernaklhead

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadRequest
import com.example.oracleapi.service.agnList.AgnListService
import com.example.oracleapi.service.typedoc.TypedocService
import org.springframework.stereotype.Service

@Service
class NaklHeadServise(
    private val naklHeadIns: NaklHeadIns,
    private val typedocService: TypedocService,
    private val agnListService: AgnListService
) {
    fun insNaklHead(request: OrderNaklHeadRequest): ResponseRN {
        if (!agnListService.existsById(request.provider)) {
            throw IllegalArgumentException("Поставщик с RN:${request.provider} не найден")
        }

        if (request.basisdoctype != null && !typedocService.existsById(request.basisdoctype)) {
            throw IllegalArgumentException("Тип документа не найден")
        }

        return naklHeadIns.take(request)
    }
}