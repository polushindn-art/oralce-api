package com.example.oracleapi.service.tsdlist

import com.example.oracleapi.common.BaseProcedure
import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.example.oracleapi.dto.userlist.RegisteredJsonResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class TsdListGetRegistered(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BaseProcedure(entityManager, objectMapper) {
    override val packageName: String = TSDLIST
    fun execute(sn: String? = null): ProcedureResult<RegisteredJsonResponse> {
        return execute("registered_json") {
            val startTime = System.currentTimeMillis()

            try {
                // Для JSON массива
                val sessions = callListFunction<Registeredjson>("registered_json", sn)
                val executionTime = System.currentTimeMillis() - startTime

                ProcedureResult.Success(
                    data = RegisteredJsonResponse(
                        sessions = sessions,  // ← список сессий
                        count = sessions.size,
                        executionTimeMs = executionTime,
                        timestamp = currentTimestamp()
                    ),
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )

            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime
                ProcedureResult.Error(
                    message = e.message ?: "Ошибка получения ТСД",
                    errorCode = "REGISTERED_JSON_ERROR",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            }
        }
    }
}