package com.example.oracleapi.service.tsdlist

import com.example.oracleapi.common.BaseProcedure
import com.example.oracleapi.common.GeneralResponse
import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class TsdListGetRegistered(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BaseProcedure(entityManager, objectMapper) {
    override val packageName: String = TSDLIST
    fun execute(sn: String? = null): GeneralResponse<JsonResponseView<Registeredjson>> {
        return execute("registered_json") {
            val startTime = System.currentTimeMillis()

            try {
                // Для JSON массива
                val sessions = callListFunction<Registeredjson>("registered_json", sn)
                val executionTime = System.currentTimeMillis() - startTime

                GeneralResponse.Success(
                    JsonResponseView(
                        sessions.size,
                        executionTime,
                        sessions
                    ),
                    executionTime,
                    currentTimestamp()
                )

            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime
                GeneralResponse.Error(
                    message = e.message ?: "Ошибка получения ТСД",
                    errorCode = "REGISTERED_JSON_ERROR",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            }
        }
    }
}