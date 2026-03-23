package com.example.oracleapi.service.public

import com.example.oracleapi.common.BaseProcedure
import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class PublicGetNomenByBarcodeProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BaseProcedure(entityManager, objectMapper) {

    override val packageName: String = PUBLIC

    fun getNomen(barcode: String): ProcedureResult<GetNomenByBarcodeResponse> {
        return execute("GETNOMENBYBARCODE") {
            val startTime = System.currentTimeMillis()
            try {
                // Используем нативный SQL для вызова функции
                val query = entityManager.createNativeQuery(
                    "SELECT PKG_PUBLIC.GETNOMENBYBARCODE(:barcode) FROM DUAL"
                )
                query.setParameter("barcode", barcode)

                val resultNumber = query.singleResult as? Number
                    ?: throw IllegalStateException("Функция не вернула идентификатор")

                val resultId = resultNumber.toLong()
                val executionTime = System.currentTimeMillis() - startTime

                ProcedureResult.Success(
                    data = GetNomenByBarcodeResponse(
                        nomen = resultId,
                        barcode = barcode,
                        executionTimeMs = executionTime,
                        timestamp = currentTimestamp()
                    ),
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime
                ProcedureResult.Error(
                    message = e.message ?: "Неизвестная ошибка",
                    errorCode = "GET_NOMEN_ERROR",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            }
        }
    }
}