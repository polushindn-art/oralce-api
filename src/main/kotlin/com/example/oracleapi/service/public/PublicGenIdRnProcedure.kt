package com.example.oracleapi.service.public

import com.example.oracleapi.common.BasePkgFunc
import com.example.oracleapi.dto.public.GenIdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class PublicGenIdRnProcedur(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkgFunc(
    entityManager,
    objectMapper
) {
    override val packageName: String = PUBLIC

    /**
     * Генерирует новый уникальный идентификатор RN
     * @return ProcedureResult с сгенерированным ID
     */
    fun generateRn(): GenIdResponse {
        val startTime = System.currentTimeMillis()

        try {
            // Вызов функции GenIDRN
            val generatedId = callFunction<Long>("GenIDRN")

            val executionTime = System.currentTimeMillis() - startTime

            return GenIdResponse.single(generatedId, executionTime)

        } catch (e: Exception) {
            throw RuntimeException("Ошибка генерации идентификатора: ${e.message}", e)
        }
    }

    /**
     * Генерирует несколько идентификаторов
     * @param count количество идентификаторов
     * @return ProcedureResult со списком сгенерированных ID
     */
    fun generateMultipleRn(count: Int): GenIdResponse {
        require(count > 0) { "Количество идентификаторов должно быть больше 0" }

        val startTime = System.currentTimeMillis()

        try {
            val ids = mutableListOf<Long>()
            repeat(count) {
                ids.add(callFunction<Long>("GenIDRN"))
            }

            val executionTime = System.currentTimeMillis() - startTime

            return GenIdResponse.multiple(ids, executionTime)
        } catch (e: Exception) {
            throw RuntimeException("Ошибка генерации $count идентификаторов: ${e.message}", e)
        }
    }
}