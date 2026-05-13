package com.example.oracleapi.service.idhead

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.common.PageResponse
import com.example.oracleapi.dto.idhead.IdheadResponse
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.dto.idhead.prihod.PrihodRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import com.example.oracleapi.dto.idhead.toResponse
import com.example.oracleapi.dto.idspec.IdheadWithSpecTsdResponse
import com.example.oracleapi.dto.idspec.toTsdResponse
import com.example.oracleapi.repository.idhead.IdheadRepository
import com.example.oracleapi.repository.idspec.IdspecRepository
import com.example.oracleapi.repository.typedoc.TypedocRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Pageable
import com.example.oracleapi.entity.Idhead
import com.example.oracleapi.service.field.FieldService
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.jpa.domain.Specification

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

        val specs = idspecRepository.findByPrnRnWithNomen(rn)
            .map { it.toTsdResponse() }

        val statusCode = fieldService.getFieldValue(Helper.idStatus, head.idStatus ?: 0)?.fieldComment

        return IdheadWithSpecTsdResponse(
            rn = head.rn!!,
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
        status: Long?,
        doctype: Long?,
        storein: Long?,
        storeout: Long?,
        pageable: Pageable
    ): Page<IdheadResponse> {
        val spec = buildSpecification(status, doctype, storein, storeout)
        return idheadRepository.findAll(spec, pageable).map { it.toResponse() }
    }

    private fun buildSpecification(
        status: Long?,
        doctypeRn: Long?,
        storeIn: Long?,
        storeOut: Long?
    ): Specification<Idhead> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            status?.let {
                predicates.add(cb.equal(root.get<Long>("idStatus"), it))
            }

            storeIn?.let {
                predicates.add(cb.equal(root.get<Long>("storein"), it))
            }

            storeOut?.let {
                predicates.add(cb.equal(root.get<Long>("storeout"), it))
            }

            doctypeRn?.let {
                predicates.add(cb.equal(root.get<Long>("doctype"), it))
            }

            cb.and(*predicates.toTypedArray())
        }
    }

}