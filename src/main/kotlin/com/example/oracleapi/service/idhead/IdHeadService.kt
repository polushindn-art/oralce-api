package com.example.oracleapi.service.idhead

import com.example.oracleapi.dto.common.PageResponse
import com.example.oracleapi.dto.idhead.IdheadResponse
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.dto.idhead.prihod.PrihodRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import com.example.oracleapi.dto.idhead.toResponse
import com.example.oracleapi.dto.idspec.IdheadWithSpecTsdResponse
import com.example.oracleapi.dto.idspec.toTsdResponse
import com.example.oracleapi.repository.idhead.IdheadRepository
import com.example.oracleapi.repository.idhead.IdheadSpecifications.byDoctypeRn
import com.example.oracleapi.repository.idhead.IdheadSpecifications.byStatus
import com.example.oracleapi.repository.idspec.IdspecRepository
import com.example.oracleapi.repository.typedoc.TypedocRepository
import org.springframework.data.jpa.domain.Specification.where
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Pageable
import com.example.oracleapi.entity.Idhead
import org.springframework.data.domain.Page

@Service
class IdHeadService(
    val prihodFunction: PrihodCreate,
    val updateStatusFun: UpdateStatus,
    val deleteFun: IdHeadDelete,
    val idheadRepository: IdheadRepository,
    val typedocRepository: TypedocRepository,
    val idspecRepository: IdspecRepository
) {fun prihodCreate(request: PrihodRequest): Long {
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

    fun getCount(): Long = idheadRepository.count()

    fun getByStatusAndDoctype(status: Long, doctype: Long): List<IdheadResponse> {
        return idheadRepository.findByIdStatusAndDoctypeEntity_Rn(status, doctype)
            .map { it.toResponse() }
    }

    fun getByFilters(status: Long?, doccode: String?): List<IdheadResponse> {
        val spec = buildSpecification(status, doccode)
        return idheadRepository.findAll(spec).map { it.toResponse() }
    }

    fun getByStatusAndDoccode(status: Long, doccode: String): List<IdheadResponse> {
        val doctypeRns = getDoctypeRnsByDoccode(doccode)
        return idheadRepository.findByIdStatusAndDoctypeEntity_RnIn(status, doctypeRns)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getDocumentWithSpecs(rn: Long): IdheadWithSpecTsdResponse {
        val head = idheadRepository.findById(rn)
            .orElseThrow { IllegalArgumentException("Документ с RN=$rn не найден") }

        val specs = idspecRepository.findByPrnRnWithNomen(rn)
            .map { it.toTsdResponse() }

        return IdheadWithSpecTsdResponse(
            rn = head.rn!!,
            docdate = head.docdate,
            docnumb = head.docnumb,
            docpref = head.docpref,
            idStatus = head.idStatus!!,
            provider = head.provider,
            storeinCode = head.storeInEntity?.storecode,
            storeoutCode = head.storeOutEntity?.storecode,
            note = head.note,
            sumprice = head.sumprice,
            specs = specs
        )
    }

    // ========== МЕТОДЫ С ПАГИНАЦИЕЙ ==========

    fun getAllWithPagination(pageable: Pageable): PageResponse<IdheadResponse> {
        val page: Page<Idhead> = idheadRepository.findAll(pageable)
        return PageResponse.fromPage(page.map { it.toResponse() })
    }

    fun getByStatusWithPagination(status: Long, pageable: Pageable): PageResponse<IdheadResponse> {
        val page: Page<Idhead> = idheadRepository.findByIdStatus(status, pageable)
        return PageResponse.fromPage(page.map { it.toResponse() })
    }

    fun getByStatusAndDoctypeWithPagination(
        status: Long,
        doctype: Long,
        pageable: Pageable
    ): PageResponse<IdheadResponse> {
        val page: Page<Idhead> = idheadRepository.findByIdStatusAndDoctypeEntity_Rn(status, doctype, pageable)
        return PageResponse.fromPage(page.map { it.toResponse() })
    }

    fun getByFiltersWithPagination(
        status: Long?,
        doccode: String?,
        pageable: Pageable
    ): PageResponse<IdheadResponse> {
        val spec = buildSpecification(status, doccode)
        val page: Page<Idhead> = idheadRepository.findAll(spec, pageable)
        return PageResponse.fromPage(page.map { it.toResponse() })
    }

    private fun getDoctypeRnsByDoccode(doccode: String): List<Long> {
        val typedocList = typedocRepository.findByDoccode(doccode)
            ?: throw IllegalArgumentException("Тип документа с кодом '$doccode' не найден")
        return typedocList.map { it.rn }
    }

    private fun buildSpecification(status: Long?, doccode: String?): org.springframework.data.jpa.domain.Specification<Idhead> {
        val doctypeRns = doccode?.let { getDoctypeRnsByDoccode(it) }
        return where(byStatus(status)).and(byDoctypeRn(doctypeRns))
    }
}