package com.example.oracleapi.service.orderpayhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertRequest
import com.example.oracleapi.dto.orderpay.OrderPayHeadInsertResponse
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class OrderPayHeadIns(
    dataSource: DataSource,
) : BasePackage(dataSource) {

    override val pkg = ORDERPAYHEAD
    override val method = "ins"
    override val count = 8

    fun take(request: OrderPayHeadInsertRequest): OrderPayHeadInsertResponse {
        return dataSource.executePrc {

            it.setLong(1, request.orderhead)
            it.setLong(2, request.status)
            it.setDateOrNull(3, request.docdate)
            it.setDateOrNull(4, request.plandate)
            it.setStringOrNull(5, request.note)
            it.setLong(6, request.orderpay)
            it.setLongOrNull(7, request.numContract)
            it.registerOutParameter(8,Types.NUMERIC)
            it.execute()

            OrderPayHeadInsertResponse(
                it.getLong(8),
            )
        }
    }

}