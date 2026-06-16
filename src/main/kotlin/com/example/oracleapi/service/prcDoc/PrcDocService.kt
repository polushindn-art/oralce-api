package com.example.oracleapi.service.prcDoc

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.prcDoc.head.PrcDocHeadDelRequest
import com.example.oracleapi.dto.prcDoc.head.PrcDocStatusRequest
import com.example.oracleapi.dto.prcDoc.head.PrcDocStatusResponse
import com.example.oracleapi.dto.prcDoc.head.PrcdocheadInsRequest
import com.example.oracleapi.dto.prcDoc.head.PrcdocheadInsResponse
import com.example.oracleapi.dto.prcDoc.spec.PrcDocSpecDelRequest
import com.example.oracleapi.dto.prcDoc.spec.PrcdocspecInsRequest
import com.example.oracleapi.service.nomncat.NomnCatService
import com.example.oracleapi.service.prefix.PrefixService
import com.example.oracleapi.service.typePrice.TypePriceService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrcDocService(
    private val prcDocHeadIns: PrcDocHeadIns,
    private val prcDocSpecIns: PrcDocSpecIns,
    private val prcDocStatusUpdate: PrcDocStatusUpdate,
    private val prefixService: PrefixService,
    private val prcDocSpecDel: PrcDocSpecDel,
    private val nomnCatService: NomnCatService,
    private val typePriceService: TypePriceService,
    private val prcDocHeadDel: PrcDocHeadDel
) {
    @Transactional
    fun prcDocIns(request: PrcdocheadInsRequest): PrcdocheadInsResponse {
        /* Для даты проверям тут, а не в dto так как пустое значение не может быть серриализовано */
        require(request.docdate != null) { "Дата документа обязательна" }
        if (!prefixService.existsByDocpref(request.docpref)) {
            throw IllegalArgumentException("Префикс ${request.docpref} не найден")
        }
        return prcDocHeadIns.take(request)
    }

    @Transactional
    fun prcSpecIns(request: PrcdocspecInsRequest): ResponseRN {

        val fieldsNomncatToCheck = listOf(
            request.nomncatCS to "CS",
            request.nomncat1 to "1",
            request.nomncat2 to "2",
            request.nomncat3 to "3",
            request.nomncat4 to "4",
            request.nomncat5 to "5"
        )

        val fieldTypePriceCheck = listOf(
            request.typepriceactioncs to "CS",
            request.typepriceaction1 to "1",
            request.typepriceaction2 to "2",
            request.typepriceaction3 to "3",
            request.typepriceaction4 to "4",
            request.typepriceaction5 to "5",
        )

        fieldsNomncatToCheck.forEach { (rn, name) ->
            rn?.takeIf { it > 0 }?.let {
                require(nomnCatService.existByRn(it)) {
                    "Индекс скидок $name с RN=$it не найден"
                }
            }
        }

        fieldTypePriceCheck.forEach { (rn, name) ->
            rn?.takeIf { it > 0 }?.let {
                require(typePriceService.existByRn(it)) {
                    "Тип ценника $name с RN=$it не найден"
                }
            }
        }

        return prcDocSpecIns.take(request)
    }

    fun statusUpdate(request: PrcDocStatusRequest): PrcDocStatusResponse {
        return prcDocStatusUpdate.take(request)
    }

    fun delHead(request: PrcDocHeadDelRequest): ResponseRN {
        return prcDocHeadDel.take(request)
    }

    fun delSpec(request: PrcDocSpecDelRequest): ResponseRN {
        return prcDocSpecDel.take(request)
    }

}