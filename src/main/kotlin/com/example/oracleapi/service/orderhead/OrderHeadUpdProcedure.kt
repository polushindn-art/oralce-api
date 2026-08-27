package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderhead.OrderHeadUpdRequest
import com.example.oracleapi.dto.orderhead.OrderHeadUpdResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Date
import java.sql.Types
import javax.sql.DataSource

@Component
class OrderHeadUpdProcedure(
    dataSource: DataSource,
) : BasePackage(dataSource)  {

    override val pkg = ORDERHEAD
    override val method = "upd"
    override val count = 28


    fun execute(request: OrderHeadUpdRequest): OrderHeadUpdResponse {
        val startTime = System.currentTimeMillis()

        var resultRn: Long
        var resultDocnumb: BigDecimal

        return dataSource.executePrc {
            var index = 1

            // 1. rn_ (IN/OUT)
            val rnParam = index++
            if (request.rn != null) {
                it.setLong(rnParam, request.rn)
            } else {
                it.setNull(rnParam, Types.BIGINT)
            }
            it.registerOutParameter(rnParam, Types.BIGINT)

            // 2. crn_
            it.setLong(index++, request.crn ?: 0L)

            // 3. doctype_
            it.setLong(index++, request.doctype ?: 0L)

            // 4. docpref_
            it.setString(index++, request.docpref ?: "")

            // 5. docnumb_ (IN/OUT)
            val docNumbParam = index++
            if (request.docnumb != null) {
                it.setBigDecimal(docNumbParam, request.docnumb)
            } else {
                it.setNull(docNumbParam, Types.NUMERIC)
            }
            it.registerOutParameter(docNumbParam, Types.NUMERIC)

            // 6. docdate_
            if (request.docdate != null) {
                it.setDate(index++, Date.valueOf(request.docdate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 7. storein_
            it.setLong(index++, request.storein ?: 0L)

            // 8. provider_
            it.setLong(index++, request.provider ?: 0L)

            // 9. ul_
            it.setLong(index++, request.ul ?: 0L)

            // 10. overhead_
            it.setBigDecimal(index++, request.overhead ?: BigDecimal.ZERO)

            // 11. note_
            it.setString(index++, request.note ?: "")

            // 12. basisdoctype_
            if (request.basisdoctype != null) {
                it.setLong(index++, request.basisdoctype)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 13. basisdocpref_
            it.setString(index++, request.basisdocpref)

            // 14. basisdocnumb_
            if (request.basisdocnumb != null) {
                it.setBigDecimal(index++, request.basisdocnumb)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 15. basisdocdate_
            if (request.basisdocdate != null) {
                it.setDate(index++, Date.valueOf(request.basisdocdate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 16. numbttn_
            if (request.numbttn != null) {
                it.setLong(index++, request.numbttn)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 17. ttip_
            if (request.ttip != null) {
                it.setLong(index++, request.ttip)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 18. nvagon_
            it.setString(index++, request.nvagon)

            // 19. toperation_
            if (request.toperation != null) {
                it.setLong(index++, request.toperation)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 20. notelogist_
            it.setString(index++, request.notelogist)

            // 21. specialmark_
            if (request.specialmark != null) {
                it.setLong(index++, request.specialmark)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 22. arrivaldate_
            if (request.arrivaldate != null) {
                it.setDate(index++, Date.valueOf(request.arrivaldate))
            } else {
                it.setNull(index++, Types.DATE)
            }

            // 23. storegate_
            if (request.storegate != null) {
                it.setLong(index++, request.storegate)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            // 24. NACL_RASH_
            if (request.naclRash != null) {
                it.setLong(index++, request.naclRash)
            } else {
                it.setNull(index++, Types.NUMERIC)
            }

            // 25. MAX_PCENT_
            if (request.maxPcent != null) {
                it.setDouble(index++, request.maxPcent)
            } else {
                it.setNull(index++, Types.DOUBLE)
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

            // Выполняем
            it.execute()

            // Получаем OUT параметры
            resultRn = it.getLong(rnParam)
            resultDocnumb = it.getBigDecimal(docNumbParam)

            val executionTime = System.currentTimeMillis() - startTime

            OrderHeadUpdResponse(
                rn = resultRn,
                docnumb = resultDocnumb,
                executionTime
            )

        }

    }

}