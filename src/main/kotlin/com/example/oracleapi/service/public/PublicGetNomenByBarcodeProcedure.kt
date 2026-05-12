package com.example.oracleapi.service.public

import com.example.oracleapi.common.BasePkgFunc
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

@Component
class PublicGetNomenByBarcodeProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkgFunc(entityManager, objectMapper) {

    override val packageName: String = PUBLIC

    fun getNomen(barcode: String): GetNomenByBarcodeResponse {
        val startTime = System.currentTimeMillis()
        try {
            // Используем нативный SQL для вызова функции
            val query = entityManager.createNativeQuery(("SELECT PKG_PUBLIC.GETNOMENBYBARCODE(:barcode) FROM DUAL"))
            query.setParameter("barcode", barcode)

            val resultNumber = query.singleResult as? Number
                ?: throw IllegalStateException("Функция не вернула идентификатор")

            val resultId = resultNumber.toLong()
            val executionTime = System.currentTimeMillis() - startTime

            return GetNomenByBarcodeResponse(
                nomen = resultId,
                barcode = barcode,
                executionTimeMs = executionTime,
                timestamp = currentTimestamp()
            )
        } catch (e: Exception) {
            throw RuntimeException("Ошибка получения номенклатуры по штрихкоду '$barcode': ${e.message}", e)
        }
    }
}