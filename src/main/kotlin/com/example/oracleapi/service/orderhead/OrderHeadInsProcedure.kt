// service/orderhead/OrderHeadInsProcedure.kt (исправленная версия)
package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePkgProc
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
) : BasePkgProc(entityManager, objectMapper) {

    override val packageName: String = ORDERHEAD

    fun execute(request: OrderHeadInsRequest): OrderHeadInsResponse {
        val startTime = System.currentTimeMillis()

        var resultDocnumb = BigDecimal.ZERO
        var resultRn = 0L

        execute("ins") {
            val sql =
                "{call $packageName.INS(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"

            entityManager.unwrap(EntityManager::class.java)
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { stmt ->
                        var index = 1

                        // IN параметры
                        stmt.setLong(index++, request.crn)
                        stmt.setLong(index++, request.doctype)
                        stmt.setString(index++, request.docpref)

                        if (request.docnumb != null) {
                            stmt.setLong(index++, request.docnumb)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        stmt.setDate(index++, java.sql.Date.valueOf(request.docdate))
                        stmt.setLong(index++, request.storein)
                        stmt.setLong(index++, request.provider)
                        stmt.setLong(index++, request.ul)
                        stmt.setBigDecimal(index++, request.overhead ?: BigDecimal.ZERO)
                        stmt.setString(index++, request.note)

                        // BASISDOCTYPE
                        if (request.basisdoctype != null && request.basisdoctype != 0L) {
                            stmt.setLong(index++, request.basisdoctype)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        stmt.setString(index++, request.basisdocpref)

                        // BASISDOCNUMB
                        if (request.basisdocnumb != null && request.basisdocnumb != BigDecimal.ZERO) {
                            stmt.setBigDecimal(index++, request.basisdocnumb)
                        } else {
                            stmt.setNull(index++, Types.DECIMAL)
                        }

                        // BASISDOCDATE
                        if (request.basisdocdate != null) {
                            stmt.setDate(index++, java.sql.Date.valueOf(request.basisdocdate))
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        // NUMBTTN
                        stmt.setLong(index++, request.numbttn)

                        // TTIP
                        if (request.ttip != null && request.ttip != 0L) {
                            stmt.setLong(index++, request.ttip)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        stmt.setString(index++, request.nvagon)

                        // TOPERATION
                        if (request.toperation != null) {
                            stmt.setLong(index++, request.toperation)
                        }

                        stmt.setString(index++, request.notelogist)

                        // SPECIALMARK
                        if (request.specialmark != null && request.specialmark != 0L) {
                            stmt.setLong(index++, request.specialmark)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        // ARRIVALDATE
                        if (request.arrivaldate != null) {
                            stmt.setDate(index++, java.sql.Date.valueOf(request.arrivaldate))
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        // STOREGATE
                        if (request.storegate != null && request.storegate != 0L) {
                            stmt.setLong(index++, request.storegate)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        // NACL_RASH
                        if (request.naclRash != null && request.naclRash != 0L) {
                            stmt.setLong(index++, request.naclRash)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        // MAX_PCENT
                        if (request.maxPcent != null && request.maxPcent != 0.0) {
                            stmt.setDouble(index++, request.maxPcent)
                        } else {
                            stmt.setNull(index++, Types.DOUBLE)
                        }

                        // RN 25
                        if (request.rn != null && request.rn != 0L) {
                            stmt.setLong(index++, request.rn)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        //plan_arrival_date_
                        if (request.planArrivalDate != null) {
                            stmt.setDate(index++, java.sql.Date.valueOf(request.planArrivalDate))
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        //nomentype_
                        stmt.setString(index++, request.nomenType)

                        //packtype_
                        stmt.setString(index++, request.packType)

                        // ISUPDATE
                        stmt.setBoolean(index++, request.isUpdate)

                        // РЕГИСТРИРУЕМ OUT параметры (ДО ВЫПОЛНЕНИЯ)
                        stmt.registerOutParameter(4, Types.DECIMAL)  // docnumb OUT
                        stmt.registerOutParameter(25, Types.BIGINT)   // rn OUT

                        // ВЫПОЛНЯЕМ процедуру
                        stmt.execute()

                        // ПОСЛЕ выполнения получаем OUT параметры
                        resultDocnumb = stmt.getBigDecimal(4) ?: BigDecimal.ZERO
                        resultRn = stmt.getLong(25)
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