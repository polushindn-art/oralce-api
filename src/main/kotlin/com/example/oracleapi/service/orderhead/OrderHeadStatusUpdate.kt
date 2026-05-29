package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderhead.OrderHeadStatusUpdateRequest
import com.example.oracleapi.dto.orderhead.OrderHeadStatusUpdateResponse
import com.example.oracleapi.entity.Field
import com.example.oracleapi.service.field.FieldService
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class OrderHeadStatusUpdate(
    dataSource: DataSource,
    private val fieldService: FieldService
) : BasePackage(dataSource) {

    override val pkg = "PKG_ORDERHEAD"
    override val method = "Status_Update"
    override val count = 2

    fun take(orderHeadStatusUpdateRequest: OrderHeadStatusUpdateRequest): OrderHeadStatusUpdateResponse {

        return dataSource.executePrc {
            it.setLong(1, orderHeadStatusUpdateRequest.rn ?: 0)
            it.setLong(2, orderHeadStatusUpdateRequest.status ?: 0)
            it.execute()
            val field = fieldService.getFieldValue(Field.ORDER_STATUS, 0)
            OrderHeadStatusUpdateResponse(
                0L,
                0L,
                field
            )
        }
    }
}