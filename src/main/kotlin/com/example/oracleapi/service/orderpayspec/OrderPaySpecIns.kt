package com.example.oracleapi.service.orderpayspec

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertResponse
import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPaySpecInsertResponse
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class OrderPaySpecIns(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = ORDERPAYSPEC
    override val method = "ins"
    override val count = 4

    fun take(request: OrderPaySpecInsertRequest): OrderPaySpecInsertResponse {
        return dataSource.executePrc {

            it.setLong(1, request.prn)
            it.setLong(2, request.nomngroup)
            it.setBigDecimal(3, request.summ)
            it.registerOutParameter(4, Types.NUMERIC)
            it.execute()
            OrderPaySpecInsertResponse(
                it.getLong(4)
            )
        }
    }

}