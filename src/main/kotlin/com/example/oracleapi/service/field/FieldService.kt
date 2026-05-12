package com.example.oracleapi.service.field

import com.example.oracleapi.dto.field.FieldResponse
import com.example.oracleapi.entity.Field
import com.example.oracleapi.repository.field.FieldRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws

@Service
class FieldService(
    private val fieldRepository: FieldRepository
) {

    @Transactional(readOnly = true)
    fun getFieldValues(fieldName: String): List<FieldResponse> {
        return fieldRepository.findByFieldNameIgnoreCaseOrderByFieldValue(fieldName)
            .map { FieldResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getFieldValue(fieldName: String, fieldValue: Long): FieldResponse {
        return requireNotNull(
            fieldRepository.findByFieldNameAndFieldValueIgnoreCase(fieldName, fieldValue)?.toResponse()
        )
        { "Не найдено значений для поля $fieldName со значением $fieldValue" }
    }

    fun Field.toResponse(): FieldResponse {
        return FieldResponse.fromEntity(this)
    }

}