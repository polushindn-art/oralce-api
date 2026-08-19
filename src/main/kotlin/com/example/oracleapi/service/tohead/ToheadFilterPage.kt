package com.example.oracleapi.service.tohead

import com.example.oracleapi.dto.tohead.ToheadDto
import com.example.oracleapi.entity.table.Tohead
import com.example.oracleapi.repository.tohead.ToheadRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class ToheadFilterPage(
    private val toHeadRepository: ToheadRepository
) {
    fun getFilters(
        doctype: BigDecimal?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        pageable: Pageable
    ): Page<ToheadDto> {
        val spec = buildSpecification(doctype, dateFrom, dateTo)
        return toHeadRepository.findAll(spec, pageable).map { ToheadDto.fromEntity(it) }
    }

    private fun buildSpecification(
        doctype: BigDecimal?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
    ): Specification<Tohead> {
        return Specification { root, query, builder ->
            val predicates = mutableListOf<Predicate>()


            doctype?.let { predicates.add(builder.equal(root.get<Long>("doctype"), it)) }

            dateFrom?.let { from ->
                predicates.add(builder.greaterThanOrEqualTo(root.get("docdate"), from.atStartOfDay()))
            }

            dateTo?.let { from ->
                predicates.add(builder.greaterThanOrEqualTo(root.get("docdate"), from.atStartOfDay()))
            }

            builder.and(*predicates.toTypedArray())

        }
    }

}