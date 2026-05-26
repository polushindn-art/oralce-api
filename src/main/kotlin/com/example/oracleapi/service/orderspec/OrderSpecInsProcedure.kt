// service/orderspec/OrderSpecInsProcedure.kt
package com.example.oracleapi.service.orderspec

import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.orderspec.OrderSpecInsRequest
import com.example.oracleapi.dto.orderspec.OrderSpecInsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.CallableStatement
import java.sql.Types

@Component
class OrderSpecInsProcedure(
    entityManager: EntityManager,
    objectMapper: ObjectMapper
) : BasePkg(entityManager, objectMapper) {

    companion object {
        const val ORDERSPEC = "PKG_ORDERSPEC.INS"
    }

    fun ins(request: OrderSpecInsRequest): OrderSpecInsResponse {
        val startTime = System.currentTimeMillis()

        var resultRn = 0L

        execute {
            val sql = ORDERSPEC.toCallPrc(46)

            entityManager.unwrap(EntityManager::class.java)
                .unwrap(org.hibernate.Session::class.java)
                .doWork { connection ->
                    connection.prepareCall(sql).use { stmt ->
                        var index = 1

                        // 1-46 параметры
                        stmt.setLong(index++, request.prn ?: 0L)
                        stmt.setLong(index++, request.nomen ?: 0L)
                        stmt.setBigDecimal(index++, request.quant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.summ ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.factquant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.quantbreak ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.notconquant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.undefinedquant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.prquant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.prsum ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.autozquant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.srbquant ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.ndsrate ?: BigDecimal.ZERO)

                        if (request.country != null) {
                            stmt.setLong(index++, request.country)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        stmt.setString(index++, request.gtd)
                        stmt.setBigDecimal(index++, request.pdpricecs ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.pdprice1 ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.pdprice2 ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.pdprice3 ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.pdprice4 ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.pdprice5 ?: BigDecimal.ZERO)

                        setLongOrNull(stmt, index++, request.pdnomncatcs)
                        setLongOrNull(stmt, index++, request.pdnomncat1)
                        setLongOrNull(stmt, index++, request.pdnomncat2)
                        setLongOrNull(stmt, index++, request.pdnomncat3)
                        setLongOrNull(stmt, index++, request.pdnomncat4)
                        setLongOrNull(stmt, index++, request.pdnomncat5)

                        stmt.setString(index++, request.notelogist)

                        if (request.whsconst != null) {
                            stmt.setLong(index++, request.whsconst)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        stmt.setString(index++, request.checkRoznPrice)

                        if (request.storein != null) {
                            stmt.setLong(index++, request.storein)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        // rn_ (IN/OUT)
                        val rnParam = index++
                        stmt.registerOutParameter(rnParam, Types.BIGINT)
                        if (request.rn != null && request.rn != 0L) {
                            stmt.setLong(rnParam, request.rn)
                        } else {
                            stmt.setNull(rnParam, Types.BIGINT)
                        }

                        stmt.setBoolean(index++, request.isUpdate)
                        stmt.setBoolean(index++, request.isWS)

                        if (request.changeOverHead != null) {
                            stmt.setLong(index++, request.changeOverHead)
                        } else {
                            stmt.setNull(index++, Types.BIGINT)
                        }

                        setLongOrNull(stmt, index++, request.dlyaKompl)
                        setLongOrNull(stmt, index++, request.komplRn)
                        setLongOrNull(stmt, index++, request.komplQty)

                        stmt.setBigDecimal(index++, request.qtyvKompl ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.calcQtyPost ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.docQtyPost ?: BigDecimal.ZERO)

                        setLongOrNull(stmt, index++, request.rnDEI)

                        stmt.setBigDecimal(index++, request.factQtyPost ?: BigDecimal.ZERO)

                        if (request.dateProduction != null) {
                            stmt.setDate(index++, java.sql.Date.valueOf(request.dateProduction))
                        } else {
                            stmt.setNull(index++, Types.DATE)
                        }

                        stmt.setBigDecimal(index++, request.quantdoc ?: BigDecimal.ZERO)
                        stmt.setBigDecimal(index++, request.summdoc ?: BigDecimal.ZERO)

                        stmt.execute()
                        resultRn = stmt.getLong(rnParam)
                    }
                }
        }

        val executionTime = System.currentTimeMillis() - startTime

        return OrderSpecInsResponse(
            rn = resultRn,
            executionTimeMs = executionTime
        )
    }

    // Вспомогательная функция
    private fun setLongOrNull(stmt: CallableStatement, index: Int, value: Long?) {
        if (value != null && value != 0L) {
            stmt.setLong(index, value)
        } else {
            stmt.setNull(index, Types.BIGINT)
        }
    }
}