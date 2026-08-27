package com.example.oracleapi.service.orderspec

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderspec.ins.OrderSpecInsRequest
import com.example.oracleapi.dto.orderspec.ins.OrderSpecInsResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Types
import javax.sql.DataSource

@Component
class OrderSpecInsProcedure(
    dataSource: DataSource
) : BasePackage(dataSource)  {

    override val pkg: String = ORDERSPEC
    override val method: String = "ins"
    override val count = 46


    fun ins(request: OrderSpecInsRequest): OrderSpecInsResponse {
        val startTime = System.currentTimeMillis()

        var resultRn = 0L

        return dataSource.executePrc {
            var index = 1

            // 1-46 параметры
            it.setLong(index++, request.prn ?: 0L)
            it.setLong(index++, request.nomen ?: 0L)
            it.setBigDecimal(index++, request.quant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.summ ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.factquant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.quantbreak ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.notconquant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.undefinedquant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.prquant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.prsum ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.autozquant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.srbquant ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.ndsrate ?: BigDecimal.ZERO)

            if (request.country != null) {
                it.setLong(index++, request.country)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            it.setString(index++, request.gtd)
            it.setBigDecimal(index++, request.pdpricecs ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.pdprice1 ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.pdprice2 ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.pdprice3 ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.pdprice4 ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.pdprice5 ?: BigDecimal.ZERO)

            it.setLongOrNull( index++, request.pdnomncatcs)
            it.setLongOrNull( index++, request.pdnomncat1)
            it.setLongOrNull( index++, request.pdnomncat2)
            it.setLongOrNull( index++, request.pdnomncat3)
            it.setLongOrNull( index++, request.pdnomncat4)
            it.setLongOrNull( index++, request.pdnomncat5)

            it.setString(index++, request.notelogist)

            if (request.whsconst != null) {
                it.setLong(index++, request.whsconst)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            it.setString(index++, request.checkRoznPrice)

            if (request.storein != null) {
                it.setLong(index++, request.storein)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            // rn_ (IN/OUT)
            val rnParam = index++
            it.registerOutParameter(rnParam, Types.BIGINT)
            if (request.rn != null && request.rn != 0L) {
                it.setLong(rnParam, request.rn)
            } else {
                it.setNull(rnParam, Types.BIGINT)
            }

            it.setBoolean(index++, request.isUpdate)
            it.setBoolean(index++, request.isWS)

            if (request.changeOverHead != null) {
                it.setLong(index++, request.changeOverHead)
            } else {
                it.setNull(index++, Types.BIGINT)
            }

            it.setLongOrNull( index++, request.dlyaKompl)
            it.setLongOrNull( index++, request.komplRn)
            it.setLongOrNull( index++, request.komplQty)

            it.setBigDecimal(index++, request.qtyvKompl ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.calcQtyPost ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.docQtyPost ?: BigDecimal.ZERO)

            it.setLongOrNull( index++, request.rnDEI)

            it.setBigDecimal(index++, request.factQtyPost ?: BigDecimal.ZERO)

            if (request.dateProduction != null) {
                it.setDate(index++, java.sql.Date.valueOf(request.dateProduction))
            } else {
                it.setNull(index++, Types.DATE)
            }

            it.setBigDecimal(index++, request.quantdoc ?: BigDecimal.ZERO)
            it.setBigDecimal(index++, request.summdoc ?: BigDecimal.ZERO)

            it.execute()
            resultRn = it.getLong(rnParam)

            val executionTime = System.currentTimeMillis() - startTime

            OrderSpecInsResponse(
                rn = resultRn,
                executionTimeMs = executionTime
            )

        }
    }
}