package com.example.oracleapi.service.prefix

import com.example.oracleapi.dto.prefix.PrefixResponse
import com.example.oracleapi.entity.table.Prefix
import com.example.oracleapi.repository.prefix.PrefixRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrefixService(
    private val prefixRepository: PrefixRepository
) {
    @Transactional(readOnly = true)
    fun existsByDocpref(prefix: String): Boolean {
        return prefixRepository.existsByDocpref(prefix)
    }

    @Transactional(readOnly = true)
    fun getAllPrefixses(pageable: Pageable): Page<PrefixResponse> {
        return prefixRepository.findAll(pageable).map { Prefix.toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getByDivicionCode(pageable: Pageable, divisionCode: String): Page<PrefixResponse> {
        return prefixRepository.findByDivisionEntity_DivisioncodeIgnoreCase(divisionCode, pageable).map { Prefix.toResponse(it) }
    }

}