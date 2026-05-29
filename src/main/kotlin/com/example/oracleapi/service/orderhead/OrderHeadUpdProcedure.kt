package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.orderhead.OrderHeadUpdRequest
import com.example.oracleapi.dto.orderhead.OrderHeadUpdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Types

@Component
class OrderHeadUpdProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkg(
    entityManager,
    objectMapper
) {
    companion object {
        const val ORDERHEAD = "PKG_ORDERHEAD.UPD"
        const val PARAM_COUNT = 28
    }

    fun execute(request: OrderHeadUpdRequest): OrderHeadUpdResponse {
        val startTime = System.currentTimeMillis()

        var resultRn = 0L
        var resultDocnumb = BigDecimal.ZERO

        execute {
            val sql = ORDERHEAD.toCallPrc(PARAM_COUNT)
            entityManager
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { stmt ->
                        with(stmt) {
                            var index = 1

                            // 1. rn_ (IN/OUT)
                            val rnParam = index++
                            if (request.rn != null) {
                                setLong(rnParam, request.rn)
                            } else {
                                setNull(rnParam, Types.BIGINT)
                            }
                            registerOutParameter(rnParam, Types.BIGINT)

                            // 2. crn_
                            setLong(index++, request.crn ?: 0L)

                            // 3. doctype_
                            setLong(index++, request.doctype ?: 0L)

                            // 4. docpref_
                            setString(index++, request.docpref ?: "")

                            // 5. docnumb_ (IN/OUT)
                            val docNumbParam = index++
                            if (request.docnumb != null) {
                                setBigDecimal(docNumbParam, request.docnumb)
                            } else {
                                setNull(docNumbParam, Types.NUMERIC)
                            }
                            registerOutParameter(docNumbParam, Types.NUMERIC)

                            // 6. docdate_
                            if (request.docdate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.docdate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 7. storein_
                            setLong(index++, request.storein ?: 0L)

                            // 8. provider_
                            setLong(index++, request.provider ?: 0L)

                            // 9. ul_
                            setLong(index++, request.ul ?: 0L)

                            // 10. overhead_
                            setBigDecimal(index++, request.overhead ?: BigDecimal.ZERO)

                            // 11. note_
                            setString(index++, request.note ?: "")

                            // 12. basisdoctype_
                            if (request.basisdoctype != null) {
                                setLong(index++, request.basisdoctype)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 13. basisdocpref_
                            setString(index++, request.basisdocpref)

                            // 14. basisdocnumb_
                            if (request.basisdocnumb != null) {
                                setBigDecimal(index++, request.basisdocnumb)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 15. basisdocdate_
                            if (request.basisdocdate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.basisdocdate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 16. numbttn_
                            if (request.numbttn != null) {
                                setLong(index++, request.numbttn)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 17. ttip_
                            if (request.ttip != null) {
                                setLong(index++, request.ttip)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 18. nvagon_
                            setString(index++, request.nvagon)

                            // 19. toperation_
                            if (request.toperation != null) {
                                setLong(index++, request.toperation)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 20. notelogist_
                            setString(index++, request.notelogist)

                            // 21. specialmark_
                            if (request.specialmark != null) {
                                setLong(index++, request.specialmark)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 22. arrivaldate_
                            if (request.arrivaldate != null) {
                                setDate(index++, java.sql.Date.valueOf(request.arrivaldate))
                            } else {
                                setNull(index++, Types.DATE)
                            }

                            // 23. storegate_
                            if (request.storegate != null) {
                                setLong(index++, request.storegate)
                            } else {
                                setNull(index++, Types.BIGINT)
                            }

                            // 24. NACL_RASH_
                            if (request.naclRash != null) {
                                setLong(index++, request.naclRash)
                            } else {
                                setNull(index++, Types.NUMERIC)
                            }

                            // 25. MAX_PCENT_
                            if (request.maxPcent != null) {
                                setDouble(index++, request.maxPcent)
                            } else {
                                setNull(index++, Types.DOUBLE)
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

                            // Выполняем
                            execute()

                            // Получаем OUT параметры
                            resultRn = getLong(rnParam)
                            resultDocnumb = getBigDecimal(docNumbParam)
                        }
                    }
                }
        }

        val executionTime = System.currentTimeMillis() - startTime

        return OrderHeadUpdResponse(
            rn = resultRn,
            docnumb = resultDocnumb,
            executionTime
        )
    }

}