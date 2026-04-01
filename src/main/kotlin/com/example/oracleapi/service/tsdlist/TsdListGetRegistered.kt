package com.example.oracleapi.service.tsdlist

import com.example.oracleapi.common.BasePkgFunc
import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class TsdListGetRegistered(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkgFunc(entityManager, objectMapper) {
    override val packageName: String = TSDLIST
    fun execute(sn: String? = null): JsonResponseView<Registeredjson> {

        val startTime = System.currentTimeMillis()

        val sessions = callListFunction<Registeredjson>("registered_json", sn)
        val executionTime = System.currentTimeMillis() - startTime

        if (sessions.isEmpty()) {
            throw RuntimeException("Не удалось получить данные по ТСД ${sn ?: ""}")
        }

        return JsonResponseView(
            sessions.size,
            executionTime,
            sessions
        )
    }
}