package com.example.oracleapi.service.tohead

import com.example.oracleapi.dto.tohead.ToHeadWithSpec
import com.example.oracleapi.dto.tohead.ToheadDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ToheadService(
    private val toheadFindRN: ToheadFindRN,
    private val toheadFilterPage: ToheadFilterPage,
    private val toHeaAndSpec: ToHeaAndSpec
) {
    fun toheadFindByRn(rn: Long): ToheadDto {
        return toheadFindRN.findToheadByRn(rn)
    }

    @Transactional(readOnly = true)
    fun getFilterPage(
        manager: BigDecimal?,
        client: BigDecimal?,
        doctype: BigDecimal?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        pageable: Pageable
    ): Page<ToheadDto> {
        return toheadFilterPage.getFilters(
            manager,
            client,
            doctype,
            dateFrom,
            dateTo,
            pageable
        )
    }

    fun toHeadWithSpecification(rn: Long): ToHeadWithSpec {
        return toHeaAndSpec.take(rn)
    }

}