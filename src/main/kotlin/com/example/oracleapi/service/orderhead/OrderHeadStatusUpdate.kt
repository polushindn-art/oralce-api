package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusUpdateRequest
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusUpdateResponse
import com.example.oracleapi.entity.table.Field
import com.example.oracleapi.service.field.FieldService
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class OrderHeadStatusUpdate(
    dataSource: DataSource,
    private val fieldService: FieldService
) : BasePackage(dataSource) {

    override val pkg = ORDERHEAD
    override val method = "Status_Update"
    override val count = 2

    fun take(request: OrderHeadStatusUpdateRequest): OrderHeadStatusUpdateResponse {

        dataSource.executePrc {
            it.setLong(1, request.rn)
            it.setLong(2, request.status)
            it.execute()
        }
        val field = fieldService.getFieldValue(Field.ORDER_STATUS, request.status)
        return OrderHeadStatusUpdateResponse(
            request.rn,
            request.status,
            field
        )
    }
}