package com.example.oracleapi.service.mark

import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.repository.mark.MarkViewRepository
import org.springframework.stereotype.Service

@Service
class MarkViewService(
    private val markViewRepository: MarkViewRepository
) {

    fun findByKm(request: MarkFindRequest): ProcedureResult<MarkFindResponse> {
        val startTime = System.currentTimeMillis()

        return try {
            val response = markViewRepository.findByKm(request.km)
            val executionTime = System.currentTimeMillis() - startTime

            if (response != null) {
                ProcedureResult.Success(
                    data = response,
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            } else {
                ProcedureResult.Error(
                    message = "Код маркировки ${request.km} не найден",
                    errorCode = "MARK_NOT_FOUND",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            }
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            ProcedureResult.Error(
                message = e.message ?: "Неизвестная ошибка",
                errorCode = "MARK_FIND_ERROR",
                executionTimeMs = executionTime,
                timestamp = currentTimestamp()
            )
        }
    }

    private fun currentTimestamp(): String {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}