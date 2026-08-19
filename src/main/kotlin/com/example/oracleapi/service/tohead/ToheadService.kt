package com.example.oracleapi.service.tohead

import com.example.oracleapi.dto.tohead.ToheadDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ToheadService(
    private val toheadFindRN: ToheadFindRN,
    private val toheadFilterPage: ToheadFilterPage
) {
    fun toheadFindByRn(rn: Long): ToheadDto {
        return toheadFindRN.findToheadByRn(rn)
    }

    fun getFilterPage(
        doctype: BigDecimal?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        pageable: Pageable
    ): Page<ToheadDto> {
        return toheadFilterPage.getFilters(
            doctype,
            dateFrom,
            dateTo,
            pageable
        )
    }

}