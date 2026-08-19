package com.example.oracleapi.service.idhead

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.idhead.IdheadResponse
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.dto.idhead.prihod.PrihodRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import com.example.oracleapi.dto.idhead.toResponse
import com.example.oracleapi.dto.idspec.IdheadWithSpecTsdResponse
import com.example.oracleapi.dto.idspec.toIdspecTsdResponse
import com.example.oracleapi.entity.table.Idhead
import com.example.oracleapi.repository.idhead.IdheadRepository
import com.example.oracleapi.repository.idspec.IdspecRepository
import com.example.oracleapi.service.field.FieldService
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.*
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class IdHeadService(
    val prihodFunction: PrihodCreate,
    val updateStatusFun: UpdateStatus,
    val deleteFun: IdHeadDelete,
    val idheadRepository: IdheadRepository,
    val idspecRepository: IdspecRepository,
    val fieldService: FieldService
) {
    fun prihodCreate(request: PrihodRequest): Long {
        return prihodFunction.createPrihodByJson(request)
    }

    fun updateStatus(request: StatusUpdateRequest) {
        return updateStatusFun.updateStatus(request)
    }

    fun delete(request: IdHeadDeleteRequest) {
        return deleteFun.delete(request)
    }

    fun getByStatus(status: Long): List<IdheadResponse> {
        return idheadRepository.findByIdStatus(status).map { it.toResponse() }
    }

    fun getCount(): Long = idheadRepository.countAllBy()

    fun getCountByStatus(status: Long): Long = idheadRepository.countByIdStatus(status)

    @Transactional(readOnly = true)
    fun getDocumentWithSpecs(rn: Long): IdheadWithSpecTsdResponse {
        val head = idheadRepository.findById(rn)
            .orElseThrow { IllegalArgumentException("Документ с RN=$rn не найден") }

        //val specs = idspecRepository.findByPrnRnWithNomen(rn).map { it.toTsdResponse() }
        //val specs = idspecRepository.findByPrnRnWithNomen2(rn)
        val specRows = idspecRepository.findByPrnRnWithNomen2(rn)
        val specs = specRows.map { it.toIdspecTsdResponse() }

        val statusCode = fieldService.getFieldValue(Helper.IDSTATUS, head.idStatus ?: 0).fieldComment

        return IdheadWithSpecTsdResponse(
            rn = head.rn,
            docdate = head.docdate,
            docnumb = head.docnumb,
            docpref = head.docpref,
            idStatus = head.idStatus!!,
            idStatusCode = statusCode,
            provider = head.provider,
            storeinCode = head.storeInEntity?.storecode,
            storeoutCode = head.storeOutEntity?.storecode,
            note = head.note,
            sumprice = head.sumprice,
            specs = specs
        )
    }

    // ========== МЕТОДЫ С ПАГИНАЦИЕЙ ==========

    fun getAllWithPagination(pageable: Pageable): Page<IdheadResponse> {
        return idheadRepository.findAll(pageable).map { it.toResponse() }
    }

    fun getByStatusWithPagination(status: Long, pageable: Pageable): Page<IdheadResponse> {
        return idheadRepository.findByIdStatus(status, pageable).map { it.toResponse() }
    }

    fun getByFiltersWithPagination(
        status: String?,
        doctype: Long?,
        docnumb: BigDecimal?,
        storein: Long?,
        storeout: Long?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?,
        pageable: Pageable
    ): Page<IdheadResponse> {
        val spec = buildSpecification(status, doctype,docnumb, storein, storeout, dateFrom, dateTo)
        return idheadRepository.findAll(spec, pageable) .map { it.toResponse() }
    }

    private fun buildSpecification(
        status: String?,
        doctypeRn: Long?,
        docnumb: BigDecimal?,
        storeIn: Long?,
        storeOut: Long?,
        dateFrom: LocalDate?,
        dateTo: LocalDate?
    ): Specification<Idhead> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Обработка нескольких статусов
            status?.let { statusStr ->
                val statusIds = statusStr.split(",")
                    .mapNotNull { it.trim().toLongOrNull() }

                when (statusIds.size) {
                    0 -> { /* Ничего не делаем */ }
                    1 -> predicates.add(cb.equal(root.get<Long>("idStatus"), statusIds.first()))
                    else -> predicates.add(root.get<Long>("idStatus").`in`(statusIds))
                }
            }

            storeIn?.let { predicates.add(cb.equal(root.get<Long>("storein"), it)) }
            storeOut?.let { predicates.add(cb.equal(root.get<Long>("storeout"), it)) }
            doctypeRn?.let { predicates.add(cb.equal(root.get<Long>("doctype"), it)) }
            docnumb?.let { predicates.add(cb.equal(root.get<BigDecimal>("docnumb"), it)) }

            // ✅ Фильтр по дате "от" (docdate >= dateFrom)
            dateFrom?.let { from ->
                predicates.add(cb.greaterThanOrEqualTo(root.get("docdate"), from.atStartOfDay()))
            }

            // ✅ Фильтр по дате "до" (docdate <= dateTo)
            dateTo?.let { to ->
                predicates.add(cb.lessThanOrEqualTo(root.get("docdate"), to.atTime(23, 59, 59)))
            }

            cb.and(*predicates.toTypedArray())

        }
    }

}