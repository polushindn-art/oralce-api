package com.example.oracleapi.service.typedoc

import com.example.oracleapi.dto.typedoc.TypedocResponse
import com.example.oracleapi.entity.table.Typedoc
import com.example.oracleapi.repository.typedoc.TypedocRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TypedocService(
    private val typedocRepository: TypedocRepository
) {

    /**
     * Получить все типы документов
     */
    @Transactional(readOnly = true)
    fun getAllTypedocs(): List<TypedocResponse> {
        return typedocRepository.findAll()
            .map { it.toResponse() }
    }

    /**
     * Получить документы по divisionCode
     * */
    @Transactional(readOnly = true)
    fun getTypeDocByDivisionCode(divisionCode: String): List<TypedocResponse> {
        return typedocRepository.findByDivision(divisionCode)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun existsById(rn: Long): Boolean {
        return typedocRepository.existsById(rn)
    }

    fun validateExists(rn: Long, message: String = "Тип документа с RN=$rn не существует") {
        if (!existsById(rn)) {
            throw IllegalArgumentException(message)
        }
    }

    fun Typedoc.toResponse(): TypedocResponse {
        return TypedocResponse.fromEntity(this)
    }

}