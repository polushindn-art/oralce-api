package com.example.oracleapi.service.tsdlist

import com.example.oracleapi.common.BasePkgFunc
import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.example.oracleapi.repository.tsd.TsdListRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class TsdListGetRegistered(
    entityManager: EntityManager,
    objectMapper: ObjectMapper,
    private val tsdListRepository: TsdListRepository
) : BasePkgFunc(entityManager, objectMapper) {
    override val packageName: String = TSDLIST

    fun execute(sn: String? = null): JsonResponseView<Registeredjson> {
        val startTime = System.currentTimeMillis()

        // Если SN указан, проверяем существует ли такой ТСД
        if (sn != null && !tsdListRepository.existsBySn(sn)) {
            val executionTime = System.currentTimeMillis() - startTime
            return JsonResponseView(
                count = 0,
                executionTimeMs = executionTime,
                row = emptyList(),
                message = "ТСД с SN=$sn не найден"
            )
        }

        try {
            val sessions = callListFunction<Registeredjson>("registered_json", sn)
            val executionTime = System.currentTimeMillis() - startTime

            // Обрабатываем null как пустой список
            val sessionsList = sessions

            val message = if (sessionsList.isEmpty()) {
                "Нет активных ТСД"
            } else {
                "Активные ТСД найдены"
            }

            return JsonResponseView(
                count = sessionsList.size,
                executionTimeMs = executionTime,
                row = sessionsList,
                message = message
            )
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            // В случае ошибки возвращаем пустой список
            return JsonResponseView(
                count = 0,
                executionTimeMs = executionTime,
                row = emptyList(),
                message = e.message
            )
        }
    }
}