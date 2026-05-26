package com.example.oracleapi.service.mark

import com.example.oracleapi.Helper
import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.mark.ParseMarkResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.sql.Types

@Component
class ParseMark(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkg(
    entityManager,
    objectMapper
) {

    companion object {
        const val PARSE_MARKING =  "parse_marking_code"
        const val GET_NOMEN_NAME = "GetNomenNameByBarcode"
    }

    fun parseMarkCode(km: String): ParseMarkResponse {
        var cis: String? = null
        var gtin: String? = null
        execute {
            val sql = PARSE_MARKING.toCallPrc(3)

            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { statement ->
                        with(statement) {
                            setString(1, km)
                            registerOutParameter(2, Types.VARCHAR)
                            registerOutParameter(3, Types.VARCHAR)
                            execute()
                            cis = getString(2)
                            gtin = getString(3)
                        }
                    }
                }
        }

        return ParseMarkResponse(
            km,
            cis,
            gtin
        )

    }


    fun getNomenName(barcode: String): ParseMarkResponse {
        var nomenname: String? = null
        execute {
            val sql2 = GET_NOMEN_NAME.toCallFnc(1)

            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql2).use { statement ->
                        with(statement) {
                            registerOutParameter(1, Types.VARCHAR)
                            setString(2, barcode)
                            execute()
                            nomenname = getString(1)
                        }
                    }
                }

        }

        return ParseMarkResponse(
            barcode,
            nomenname,
            nomenname
        )
    }

}