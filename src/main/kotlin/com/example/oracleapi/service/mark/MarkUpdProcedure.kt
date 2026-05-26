package com.example.oracleapi.service.mark

import com.example.oracleapi.common.BasePkg
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
) : BasePkg(entityManager, objectMapper) {

    companion object {
        const val MARK = "PKG_MARK.upd"
    }

    fun execute(request: MarkUpdRequest): MarkUpdResponse {
        execute {

            val sql = MARK.toCallPrc(6)
            val jsonString = objectMapper.writeValueAsString(request.json)

            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { statement ->
                        with(statement) {
                            setString(1, request.km)
                            setString(2, jsonString)
                            setString(3, request.table)
                            setLong(4, request.tablern)
                            setInt(5, request.status)
                            setString(6, request.note)
                            execute()
                        }
                    }


                }
        }
        return MarkUpdResponse(
            true,
            "",
            ""
        )
    }
}

