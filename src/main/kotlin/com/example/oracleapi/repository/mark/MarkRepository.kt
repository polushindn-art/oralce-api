package com.example.oracleapi.repository.mark

import com.example.oracleapi.dto.mark.MarkFindResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import java.sql.Clob
import org.springframework.cache.annotation.Cacheable

@Repository
class MarkRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private val objectMapper = ObjectMapper()

    private fun minifyJson(jsonString: String?): String? {
        if (jsonString == null) return null

        return try {
            val jsonNode = objectMapper.readTree(jsonString)
            objectMapper.writeValueAsString(jsonNode)
        } catch (_: Exception) {
            // Если невалидный JSON, возвращаем как есть
            jsonString
        }
    }

    private fun clobToMinifiedString(clob: Clob?): String? {
        return clob?.let {
            val length = it.length().toInt()
            if (length > 0) {
                val jsonString = it.getSubString(1, length)
                minifyJson(jsonString)
            } else {
                null
            }
        }
    }

    @Cacheable(value = ["markCache"], key = "#km") //КЭШРУЕМ для повторного запроса
    fun findByKm(km: String): MarkFindResponse? {
        val query = entityManager.createNativeQuery(
            """
            SELECT 
                rn,
                TO_CHAR(date_add, 'YYYY-MM-DD HH24:MI:SS') as date_add,
                km,
                json,
                cis,
                gtin,
                status,
                nomen,
                state_mark,
                note
            FROM qreal.v_mark_find
            WHERE km = :km
            """
        )
        query.setParameter("km", km)

        @Suppress("UNCHECKED_CAST")
        val resultList = query.resultList as List<Array<Any>>

        return if (resultList.isNotEmpty()) {
            val row = resultList[0]
            MarkFindResponse(
                rn = (row[0] as? Number)?.toLong(),
                dateAdd = row[1] as? String,
                km = row[2] as String,
                json = when (val data = row[3]) {
                    is Clob -> clobToMinifiedString(data)
                    is String -> minifyJson(data)
                    else -> null
                },
                cis = row[4] as? String,
                gtin = row[5] as? String,
                status = (row[6] as? Number)?.toInt(),
                nomen = row[7] as? Long,
                stateMark = row[8] as? String,
                note = row[9] as? String
            )
        } else {
            null
        }
    }
}