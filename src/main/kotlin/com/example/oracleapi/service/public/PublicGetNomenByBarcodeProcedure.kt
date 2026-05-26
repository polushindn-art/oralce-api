package com.example.oracleapi.service.public

import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.sql.Types

@Component
class PublicGetNomenByBarcodeProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkg(entityManager, objectMapper) {

    companion object {
        const val PUBLIC_GETNOMEN = "PKG_PUBLIC.GETNOMENBYBARCODE"
    }

    fun getNomenByBarcodeProcedure(barcode: String): GetNomenByBarcodeResponse {
        val sql = PUBLIC_GETNOMEN.toCallFnc(1)
        var nomen = 0L
        execute {
            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { statement ->
                        with(statement) {
                            registerOutParameter(1, Types.VARCHAR)
                            setString(2, barcode)
                            execute()
                            nomen = getLong(1)
                        }
                    }
                }
        }
        return GetNomenByBarcodeResponse(
            nomen,
            barcode
        )
    }

}