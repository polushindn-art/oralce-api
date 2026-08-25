package com.example.oracleapi.service.ordernaklhead

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadDelRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklSpecRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklSpecResponse
import com.example.oracleapi.repository.ordernaklhead.OrdernaklheadRepository
import com.example.oracleapi.service.agnList.AgnListService
import com.example.oracleapi.service.country.CountryService
import com.example.oracleapi.service.nomnlist.NomnlistService
import com.example.oracleapi.service.orderhead.OrderHeadService
import com.example.oracleapi.service.typedoc.TypedocService
import org.springframework.stereotype.Service

@Service
class NaklHeadService(
    private val naklHeadIns: NaklHeadIns,
    private val naklSpecIns: NaklSpecIns,
    private val naklHeadDel: NaklHeadDel,
    private val typedocService: TypedocService,
    private val agnListService: AgnListService,
    private val countryService: CountryService,
    private val nomnlistService: NomnlistService,
    private val ordernaklheadRepository: OrdernaklheadRepository,
    private val orderHeadServise: OrderHeadService
) {
    /** Создание заголовка */
    fun insNaklHead(request: OrderNaklHeadRequest): ResponseRN {

        if (!orderHeadServise.existByRn(request.prn)) {
            throw IllegalArgumentException("Заказ с RN: ${request.prn} не найден")
        }

        if (!agnListService.existsById(request.provider)) {
            throw IllegalArgumentException("Поставщик с RN:${request.provider} не найден")
        }

        if (request.basisdoctype != null && !typedocService.existsById(request.basisdoctype)) {
            throw IllegalArgumentException("Тип документа не найден")
        }

        return naklHeadIns.take(request)
    }

    /** Создание спецификации */
    fun insNaklSpec(request: OrderNaklSpecRequest): OrderNaklSpecResponse {

        if (!ordernaklheadRepository.existsByRn(request.prn)) {
            throw IllegalArgumentException("Заголовок документа с RN: ${request.prn} не найден")
        }

        if (!countryService.existsByRn(request.country)) {
            throw IllegalArgumentException("Страна с RN:${request.country} не существует")
        }

        if (!nomnlistService.existsByRn(request.nomen)) {
            throw IllegalArgumentException("Номенклатура с RN:${request.nomen} не существует")
        }

        return naklSpecIns.take(request)
    }

    /** Удаление заголовка */
    fun delHead(request: OrderNaklHeadDelRequest): ResponseRN {
        return naklHeadDel.take(request)
    }

}