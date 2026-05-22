package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePkgProc
import com.example.oracleapi.dto.orderhead.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.OrderHeadInsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.CallableStatement
import java.sql.Types
import java.time.LocalDate

@Component
class OrderHeadInsProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkgProc(entityManager, objectMapper) {

    override val packageName: String = ORDERHEAD

    private fun setDateParam(stmt: CallableStatement, index: Int, date: LocalDate?) {
        if (date != null) {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")
            stmt.setString(index, date.format(formatter))
        } else {
            stmt.setNull(index, Types.VARCHAR)
        }
    }

    fun execute(request: OrderHeadInsRequest): OrderHeadInsResponse {
        val startTime = System.currentTimeMillis()

        var resultDocnumb = BigDecimal.ZERO
        var resultRn = 0L


        execute("ins") {
            // 28 параметров (согласно Toad примеру)
            val sql =
                "{call $packageName.INS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"

            entityManager.unwrap(EntityManager::class.java)
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    // ✅ Устанавливаем NLS_DATE_FORMAT для этой сессии
                    connection.createStatement().use { nlsStmt ->
                        nlsStmt.execute("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'")
                        System.err.println("NLS_DATE_FORMAT set to YYYY-MM-DD")
                    }
                    connection.prepareCall(sql).use { stmt ->
                        var index = 1

                        // 1. crn_
                        stmt.setLong(index++, request.crn ?: 0L)

                        // 2. doctype_
                        stmt.setLong(index++, request.doctype ?: 0L)

                        // 3. docpref_
                        stmt.setString(index++, request.docpref ?: "")

                        // 4. docnumb_ (IN/OUT)
                        val docNumbParam = index++
                        stmt.registerOutParameter(docNumbParam, Types.FLOAT)
                        if (request.docnumb != null) {
                            stmt.setBigDecimal(docNumbParam, request.docnumb)
                        } else {
                            stmt.setNull(docNumbParam, Types.FLOAT)
                        }

                        // 5. docdate_
                        if (request.docdate != null) {
                            setDateParam(stmt, index++, request.docdate)
                        } else {
                            stmt.setNull(index++, Types.DATE)
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
                            setDateParam(stmt, index++, request.basisdocdate)
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        // 15. numbttn_
                        if (request.numbttn != null) {
                            stmt.setLong(index++, request.numbttn)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        // 16. ttip_
                        if (request.ttip != null) {
                            stmt.setLong(index++, request.ttip)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        // 17. nvagon_
                        stmt.setString(index++, request.nvagon)

                        // 18. toperation_
                        if (request.toperation != null) {
                            stmt.setLong(index++, request.toperation)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        // 19. notelogist_
                        stmt.setString(index++, request.notelogist)

                        // 20. specialmark_
                        if (request.specialmark != null) {
                            stmt.setLong(index++, request.specialmark)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        // 21. arrivaldate_
                        if (request.arrivaldate != null) {
                            setDateParam(stmt, index++, request.arrivaldate)
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        // 22. storegate_
                        if (request.storegate != null) {
                            stmt.setLong(index++, request.storegate)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        // 23. NACL_RASH_
                        if (request.naclRash != null) {
                            stmt.setLong(index++, request.naclRash)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        // 24. MAX_PCENT_
                        if (request.maxPcent != null) {
                            stmt.setDouble(index++, request.maxPcent)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        // 25. rn_ (IN/OUT)
                        val rnParam = index++
                        if (request.rn != null) {
                            stmt.setLong(rnParam, request.rn)
                        } else {
                            stmt.setNull(rnParam, Types.BIGINT)
                        }

                        // 26. plan_arrival_date_
                        if (request.planArrivalDate != null) {
                            setDateParam(stmt, index++, request.planArrivalDate)
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        // 27. nomentype_
                        stmt.setString(index++, request.nomenType)

                        // 28. packtype_
                        stmt.setString(index++, request.packType)

                        // 29. isUpdate (последний параметр)
                        stmt.setBoolean(index++, request.isUpdate)

                        System.err.println("=== ПАРАМЕТРЫ ВЫЗОВА ===")
                        System.err.println("docdate = ${request.docdate}")
                        System.err.println("arrivaldate = ${request.arrivaldate}")
                        System.err.println("basisdocdate = ${request.basisdocdate}")
                        System.err.println("planArrivalDate = ${request.planArrivalDate}")
                        System.err.println("======================")

                        // Выполняем
                        stmt.execute()

                        // Получаем OUT параметры
                        resultDocnumb = stmt.getBigDecimal(docNumbParam)
                        resultRn = stmt.getLong(rnParam)
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