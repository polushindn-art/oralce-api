package com.example.oracleapi.service.tohead

import com.example.oracleapi.dto.agn.AgnListSimpleDto
import com.example.oracleapi.dto.tohead.ToHeadWithSpec
import com.example.oracleapi.dto.tospec.TospecDto
import com.example.oracleapi.repository.tohead.ToheadRepository
import com.example.oracleapi.repository.tospec.TospecRepository
import org.springframework.stereotype.Component

@Component
class ToHeaAndSpec(
    private val toHeadRepository: ToheadRepository,
    private val tospecRepository: TospecRepository
) {
    fun take(rn: Long): ToHeadWithSpec {
        val head = toHeadRepository.findById(rn).orElseThrow { IllegalArgumentException("Документ с RN=$rn не найден") }

        val specRows = tospecRepository.findByPrn(rn)

        val spec = specRows.map { TospecDto.fromEntity(it) }

        return ToHeadWithSpec(
            rn = head.rn,
            doctype = head.doctypeEntity?.rn,
            docpref = head.docpref,
            docdate = head.docdate,
            client = head.client,
            clientEntity = head.clientEntity?.let { AgnListSimpleDto.fromEntity(it) },
            paydoc = head.paydocEntity?.rn,
            docnumb = head.docnumb,
            sumdoc = head.sumdoc,
            agnlist = head.agnlist,
            spec = spec
        )

    }
}
