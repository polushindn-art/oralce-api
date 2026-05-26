package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Types

@Component
class OrderHeadInsProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkg(entityManager, objectMapper) {

    //override val packageName: String = ORDERHEAD

    companion object {
        const val ORDERHEAD = "PKG_ORDERHEAD.INS"
    }

    fun execute(request: OrderHeadInsRequest): OrderHeadInsResponse {
        val startTime = System.currentTimeMillis()

        var resultDocnumb = BigDecimal.ZERO
        var resultRn = 0L


        execute {

            val sql = ORDERHEAD.toCallPrc(29)

            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { stmt ->
                        with(stmt) {

                            var index = 1
                            // 1. crn_
                            setLong(index++, request.crn ?: 0L)

                            // 2. doctype_
                            setLong(index++, request.doctype ?: 0L)

                            // 3. docpref_
                            setString(index++, request.docpref ?: "")

                            // 4. docnumb_ (IN/OUT)
                            val docNumbParam = index++
                            registerOutParameter(docNumbParam, Types.FLOAT)
                            if (request.docnumb != null) {
                                setBigDecimal(docNumbParam, request.docnumb)
                            } else {
                                setNull(docNumbParam, Types.FLOAT)
                            }

                            // 5. docdate_
                            if (request.docdate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.docdate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 6. storein_
                            stmt.setLong(index++, request.storein ?: 0L)

                            // 7. provider_
                            stmt.setLong(index++, request.provider ?: 0L)

                            // 8. ul_
                            stmt.setLong(index++, request.ul ?: 0L)

                            // 9. overhead_
                            stmt.setBigDecimal(index++, request.overhead ?: BigDecimal.ZERO)

                            // 10. note_
                            stmt.setString(index++, request.note ?: "")

                            // 11. basisdoctype_
                            if (request.basisdoctype != null) {
                                stmt.setLong(index++, request.basisdoctype)
                            } else {
                                stmt.setNull(index++, Types.DOUBLE)
                            }

                            // 12. basisdocpref_
                            stmt.setString(index++, request.basisdocpref)

                            // 13. basisdocnumb_
                            if (request.basisdocnumb != null) {
                                stmt.setBigDecimal(index++, request.basisdocnumb)
                            } else {
                                stmt.setNull(index++, Types.DOUBLE)
                            }

                            // 14. basisdocdate_
                            if (request.basisdocdate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.basisdocdate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 15. numbttn_
                            if (request.numbttn != null) {
                                setLong(index++, request.numbttn)
                            } else {
                                setNull(index++, Types.DOUBLE)
                            }

                            // 16. ttip_
                            if (request.ttip != null) {
                                setLong(index++, request.ttip)
                            } else {
                                setNull(index++, Types.DOUBLE)
                            }

                            // 17. nvagon_
                            setString(index++, request.nvagon)

                            // 18. toperation_
                            if (request.toperation != null) {
                                setLong(index++, request.toperation)
                            } else {
                                setNull(index++, Types.DOUBLE)
                            }

                            // 19. notelogist_
                            setString(index++, request.notelogist)

                            // 20. specialmark_
                            if (request.specialmark != null) {
                                setLong(index++, request.specialmark)
                            } else {
                                setNull(index++, Types.DOUBLE)
                            }

                            // 21. arrivaldate_
                            if (request.arrivaldate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.arrivaldate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 22. storegate_
                            if (request.storegate != null) {
                                setLong(index++, request.storegate)
                            } else {
                                setNull(index++, Types.BIGINT)
                            }

                            // 23. NACL_RASH_
                            if (request.naclRash != null) {
                                setLong(index++, request.naclRash)
                            } else {
                                setNull(index++, Types.BIGINT)
                            }

                            // 24. MAX_PCENT_
                            if (request.maxPcent != null) {
                                setDouble(index++, request.maxPcent)
                            } else {
                                setNull(index++, Types.DOUBLE)
                            }

                            // 25. rn_ (IN/OUT)
                            val rnParam = index++
                            if (request.rn != null) {
                                setLong(rnParam, request.rn)
                            } else {
                                setNull(rnParam, Types.BIGINT)
                            }

                            // 26. plan_arrival_date_
                            if (request.planArrivalDate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.planArrivalDate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 27. nomentype_
                            setString(index++, request.nomenType)

                            // 28. packtype_
                            setString(index++, request.packType)

                            // 29. isUpdate (последний параметр)
                            setBoolean(index++, request.isUpdate)

                            // Выполняем
                            execute()

                            // Получаем OUT параметры
                            resultDocnumb = stmt.getBigDecimal(docNumbParam)
                            resultRn = stmt.getLong(rnParam)
                        }
                    }
                }
        }

        val executionTime = System.currentTimeMillis() - startTime

        return OrderHeadInsResponse(
            docnumb = resultDocnumb,
            rn = resultRn,
            executionTimeMs = executionTime
        )
    }
}