package com.example.oracleapi.service.public

import com.example.oracleapi.common.BaseProcedure
import com.example.oracleapi.common.GeneralResponse
import com.example.oracleapi.dto.public.GenIdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class PublicGenIdRnProcedur(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BaseProcedure(
    entityManager,
    objectMapper
) {
    override val packageName: String = PUBLIC
    /**
     * Генерирует новый уникальный идентификатор RN
     * @return ProcedureResult с сгенерированным ID
     */
    fun generateRn(): GeneralResponse<GenIdResponse> {
        return execute("generate_rn") {
            val startTime = System.currentTimeMillis()

            try {
                // Вызов функции GenIDRN
                val generatedId = callFunction<Long>("GenIDRN")

                val executionTime = System.currentTimeMillis() - startTime

                GeneralResponse.Success(
                    data = GenIdResponse.single(generatedId,executionTime),
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )

            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime
                GeneralResponse.Error(
                    message = e.message ?: "Ошибка генерации идентификатора",
                    errorCode = "ID_GENERATION_ERROR",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            }
        }
    }

    /**
     * Генерирует несколько идентификаторов
     * @param count количество идентификаторов
     * @return ProcedureResult со списком сгенерированных ID
     */
    fun generateMultipleRn(count: Int): GeneralResponse<GenIdResponse> {
        return execute("generate_multiple_rn") {
            val startTime = System.currentTimeMillis()

            try {
                val ids = mutableListOf<Long>()
                repeat(count) {
                    ids.add(callFunction<Long>("GenIDRN"))
                }

                val executionTime = System.currentTimeMillis() - startTime

                GeneralResponse.Success(
                    data = GenIdResponse.multiple(
                        ids = ids,
                        executionTimeMs = executionTime,
                    ),
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )

            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime
                GeneralResponse.Error(
                    message = e.message ?: "Ошибка генерации идентификаторов",
                    errorCode = "MULTIPLE_ID_GENERATION_ERROR",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            }
        }
    }
}