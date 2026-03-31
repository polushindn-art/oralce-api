package com.example.oracleapi.service.mark

import com.example.oracleapi.Helper
import com.example.oracleapi.common.GeneralResponse
import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.repository.mark.MarkViewRepository
import org.springframework.stereotype.Service

@Service
class MarkViewService(
    private val markViewRepository: MarkViewRepository
) {

    fun findByKm(request: MarkFindRequest): GeneralResponse<MarkFindResponse> {
        val startTime = System.currentTimeMillis()

        return try {
            val response = markViewRepository.findByKm(request.km)
            val executionTime = System.currentTimeMillis() - startTime

            if (response != null) {
                GeneralResponse.Success(
                    data = response,
                    executionTimeMs = executionTime,
                    timestamp = Helper.currentTimestamp()
                )
            } else {
                GeneralResponse.Error(
                    message = "Код маркировки ${request.km} не найден",
                    errorCode = "MARK_NOT_FOUND",
                    executionTimeMs = executionTime,
                    timestamp = Helper.currentTimestamp()
                )
            }
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            GeneralResponse.Error(
                message = e.message ?: "Неизвестная ошибка",
                errorCode = "MARK_FIND_ERROR",
                executionTimeMs = executionTime,
                timestamp = Helper.currentTimestamp()
            )
        }
    }

}