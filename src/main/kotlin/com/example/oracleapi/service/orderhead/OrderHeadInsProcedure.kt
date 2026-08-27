package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderhead.ins.OrderHeadInsRequest
import com.example.oracleapi.dto.orderhead.ins.OrderHeadInsResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Date
import java.sql.Types
import javax.sql.DataSource

@Component
class OrderHeadInsProcedure(
    dataSource: DataSource,
) : BasePackage(dataSource) {

    override val pkg: String = ORDERHEAD
    override val method = "ins"
    override val count = 29

    fun execute(request: OrderHeadInsRequest): OrderHeadInsResponse {
        val startTime = System.currentTimeMillis()

        var resultDocnumb: BigDecimal
        var resultRn: Long

        return dataSource.executePrc {
            var index = 1
            // 1. crn_
            it.setLong(index++, request.crn ?: 0L)

            // 2. doctype_
            it.setLong(index++, request.doctype ?: 0L)

            // 3. docpref_
            it.setString(index++, request.docpref ?: "")

            // 4. docnumb_ (IN/OUT)
            val docNumbParam = index++
            it.registerOutParameter(docNumbParam, Types.FLOAT)
            if (request.docnumb != null) {
                it.setBigDecimal(docNumbParam, request.docnumb)
            } else {
                it.setNull(docNumbParam, Types.FLOAT)
            }

            // 5. docdate_
            if (request.docdate != null) {
                it.setDate(index++, Date.valueOf(request.docdate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 6. storein_
            it.setLong(index++, request.storein ?: 0L)

            // 7. provider_
            it.setLong(index++, request.provider ?: 0L)

            // 8. ul_
            it.setLong(index++, request.ul ?: 0L)

            // 9. overhead_
            it.setBigDecimal(index++, request.overhead ?: BigDecimal.ZERO)

            // 10. note_
            it.setString(index++, request.note ?: "")

            // 11. basisdoctype_
            if (request.basisdoctype != null) {
                it.setLong(index++, request.basisdoctype)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 12. basisdocpref_
            it.setString(index++, request.basisdocpref)

            // 13. basisdocnumb_
            if (request.basisdocnumb != null) {
                it.setBigDecimal(index++, request.basisdocnumb)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 14. basisdocdate_
            if (request.basisdocdate != null) {
                it.setDate(index++, Date.valueOf(request.basisdocdate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 15. numbttn_
            if (request.numbttn != null) {
                it.setLong(index++, request.numbttn)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 16. ttip_
            if (request.ttip != null) {
                it.setLong(index++, request.ttip)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 17. nvagon_
            it.setString(index++, request.nvagon)

            // 18. toperation_
            if (request.toperation != null) {
                it.setLong(index++, request.toperation)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 19. notelogist_
            it.setString(index++, request.notelogist)

            // 20. specialmark_
            if (request.specialmark != null) {
                it.setLong(index++, request.specialmark)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 21. arrivaldate_
            if (request.arrivaldate != null) {
                it.setDate(index++, Date.valueOf(request.arrivaldate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 22. storegate_
            if (request.storegate != null) {
                it.setLong(index++, request.storegate)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            // 23. NACL_RASH_
            if (request.naclRash != null) {
                it.setLong(index++, request.naclRash)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            // 24. MAX_PCENT_
            if (request.maxPcent != null) {
                it.setDouble(index++, request.maxPcent)
            } else {
                it.setNull(index++, Types.DOUBLE)
            }

            // 25. rn_ (IN/OUT)
            val rnParam = index++
            if (request.rn != null) {
                it.setLong(rnParam, request.rn)
            } else {
                it.setNull(rnParam, Types.BIGINT)
            }

            // 26. plan_arrival_date_
            if (request.planArrivalDate != null) {
                it.setDate(index++, Date.valueOf(request.planArrivalDate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 27. nomentype_
            it.setString(index++, request.nomenType)

            // 28. packtype_
            it.setString(index++, request.packType)

            // 29. isUpdate (последний параметр)
            it.setBoolean(index++, request.isUpdate)

            // Выполняем
            it.execute()

            // Получаем OUT параметры
            resultDocnumb = it.getBigDecimal(docNumbParam)
            resultRn = it.getLong(rnParam)

            val executionTime = System.currentTimeMillis() - startTime

            OrderHeadInsResponse(
                docnumb = resultDocnumb,
                rn = resultRn,
                executionTimeMs = executionTime
            )

        }
        
    }
}