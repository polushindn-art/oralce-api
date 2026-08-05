package com.example.oracleapi.service.prcPriceOS

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.prcPriceOS.PrcPriceOSRequest
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class PrcPriceOsIns(
    dataSource: DataSource,
) : BasePackage(dataSource) {
    override val pkg = PRCPRICEOS
    override val method = "ins_api"
    override val count = 8

    fun take(request: PrcPriceOSRequest): ResponseRN {
        return dataSource.executePrc {

            it.registerOutParameter(1, Types.NUMERIC)
            it.setLong(2, request.nomen)
            it.setBigDecimal(3,request.price)
            it.setLong(4,request.dscgroup)
            it.setDate(5, java.sql.Date.valueOf(request.futuredate))
            it.setLong(6, request.region)
            it.setLong(7,request.prcdochead)
            it.setBigDecimal(8,request.percent)

            it.execute()

            ResponseRN(
                it.getLong(1)
            )
        }
    }

}