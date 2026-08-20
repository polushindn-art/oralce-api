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
        manager: BigDecimal?,
        client: BigDecimal?,
        doctype: BigDecimal?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        pageable: Pageable
    ): Page<ToheadDto> {
        val spec = buildSpecification(manager, client, doctype, dateFrom, dateTo)
        return toHeadRepository.findAll(spec, pageable).map { ToheadDto.fromEntity(it) }
    }

    private fun buildSpecification(
        manager: BigDecimal?,
        client: BigDecimal?,
        doctype: BigDecimal?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
    ): Specification<Tohead> {
        return Specification { root, _, builder ->
            val predicates = mutableListOf<Predicate>()

            manager?.let { predicates.add(builder.equal(root.get<String>("manager"), it)) }

            client?.let { predicates.add(builder.equal(root.get<String>("client"), it)) }

            doctype?.let { predicates.add(builder.equal(root.get<Long>("doctype"), it)) }

            // ✅ Фильтр по дате "от" (docdate >= dateFrom)
            dateFrom?.let { from ->
                predicates.add(builder.greaterThanOrEqualTo(root.get("docdate"), from.atStartOfDay()))
            }

            // ✅ Фильтр по дате "до" (docdate <= dateTo)
            dateTo?.let { to ->
                predicates.add(builder.lessThanOrEqualTo(root.get("docdate"), to.atTime(23, 59, 59)))
            }

            builder.and(*predicates.toTypedArray())

        }
    }

}