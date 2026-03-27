package com.example.oracleapi.service.mark

import com.example.oracleapi.common.BaseProcedure
import com.example.oracleapi.common.ProcedureResult
import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component


/**
 * Реализация процедуры PKG_MARK.UPD
 */
@Component
class MarkUpdProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper,
    private val cacheManager: CacheManager
) : BaseProcedure(entityManager, objectMapper) {
    override val packageName: String = MARK
    //@Transactional
    fun execute(request: MarkUpdRequest): ProcedureResult<MarkUpdResponse> {
        return execute("UPD") {
            val startTime = System.currentTimeMillis()

            try {
                val jsonString = objectMapper.writeValueAsString(request.json)

                // Используем именованную процедуру из entity
                val query = entityManager.createNamedStoredProcedureQuery("MarkProcedure.UPD")

                // Устанавливаем параметры
                query.setParameter("KM_", request.km)
                query.setParameter("JSON_", jsonString)
                query.setParameter("TABLE_", request.table)
                query.setParameter("TABLERN_", request.tablern.toLong())
                query.setParameter("STATUS_", request.status)
                query.setParameter("NOTE_", request.note)

                // Выполняем
                query.execute()

                // Очищам КЭШ для запроса КМ
                cacheManager.getCache("markCache")?.evict(request.km)

                val executionTime = System.currentTimeMillis() - startTime

                ProcedureResult.Success(
                    data = MarkUpdResponse(
                        success = true,
                        message = "Метка успешно обновлена",
                        km = request.km,
                        executionTimeMs = executionTime,
                        timestamp = currentTimestamp()
                    ),
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )

            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime

                ProcedureResult.Error(
                    message = e.message ?: "Unknown error",
                    errorCode = "MARK_UPD_ERROR",
                    executionTimeMs = executionTime,
                    timestamp = currentTimestamp()
                )
            } finally {
                // Здесь можно освободить ресурсы если нужно
            }
        }
    }
}