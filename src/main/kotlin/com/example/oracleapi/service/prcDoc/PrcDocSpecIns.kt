package com.example.oracleapi.service.prcDoc

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.prcDoc.spec.PrcdocspecInsRequest
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class PrcDocSpecIns(dataSource: DataSource) : BasePackage(dataSource) {

    override val pkg = "PKG_PRCDOCSPEC"
    override val method = "ins"
    override val count = 29

    fun take(request: PrcdocspecInsRequest): RnResponse {
        return dataSource.executePrc { stmt ->
            var idx = 1

            // 1..2
            stmt.setLong(idx++, request.prn)
            stmt.setLong(idx++, request.nomen)

            // 3..8 (nomncat)
            stmt.setLongOrNull(idx++, request.nomncat1)
            stmt.setLongOrNull( idx++, request.nomncat2)
            stmt.setLongOrNull( idx++, request.nomncat3)
            stmt.setLongOrNull( idx++, request.nomncat4)
            stmt.setLongOrNull( idx++, request.nomncat5)
            stmt.setLongOrNull( idx++, request.nomncatCS)

            // 9..14 (price)
            stmt.setBigDecimal(idx++, request.pricecs)
            stmt.setBigDecimal(idx++, request.price1)
            stmt.setBigDecimal(idx++, request.price2)
            stmt.setBigDecimal(idx++, request.price3)
            stmt.setBigDecimal(idx++, request.price4)
            stmt.setBigDecimal(idx++, request.price5)

            // 15..19
            stmt.setLong(idx++, request.enabled)
            stmt.setLong(idx++, request.overhaul)
            stmt.setLong(idx++, request.kopeck)
            stmt.setLong(idx++, request.whsconst)
            stmt.setString(idx++, request.checkRoznPrice)

            // 20
            stmt.setBigDecimalOrNull( idx++, request.overhaulpricepr)

            // 21..26 (typepriceaction)
            stmt.setLongOrNull( idx++, request.typepriceactioncs)
            stmt.setLongOrNull( idx++, request.typepriceaction1)
            stmt.setLongOrNull( idx++, request.typepriceaction2)
            stmt.setLongOrNull( idx++, request.typepriceaction3)
            stmt.setLongOrNull( idx++, request.typepriceaction4)
            stmt.setLongOrNull( idx++, request.typepriceaction5)

            // 27. rn_ (IN OUT)
            val rnPos = idx
            if (request.rn != null) {
                stmt.setLong(rnPos, request.rn)
            } else {
                stmt.setNull(rnPos, Types.NUMERIC)
            }
            stmt.registerOutParameter(rnPos, Types.NUMERIC)
            idx++

            // 28. isUpdate
            stmt.setBoolean(idx++, request.isUpdate)

            // 29. isWS
            stmt.setBoolean(idx++, request.isWS)

            stmt.execute()

            RnResponse(
                rn = stmt.getLong(rnPos)
            )
        }
    }
}