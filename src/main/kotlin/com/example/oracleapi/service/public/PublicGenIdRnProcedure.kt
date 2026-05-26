package com.example.oracleapi.service.public

import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.public.GenIdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.sql.Types

@Component
class PublicGenIdRnProcedur(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkg(
    entityManager,
    objectMapper
) {

    companion object {
        const val PUBLIC_GENID = "PKG_PUBLIC.GenIDRN"
    }

    /**
     * Генерирует новый уникальный идентификатор RN
     * @return ProcedureResult с сгенерированным ID
     */
    fun generateRn(): GenIdResponse {
        val sql = PUBLIC_GENID.toCallFnc(0)
        var rn = 0L
        execute {
            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { statement ->
                        with(statement) {
                            registerOutParameter(1, Types.DOUBLE)
                            execute()
                            rn = getLong(1)
                        }
                    }
                }
        }
        return GenIdResponse.single(rn, 0)
    }
}