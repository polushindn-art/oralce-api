package com.example.oracleapi.service.orderhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderhead.status.OrderHeadStatusResponse
import com.example.oracleapi.entity.Field
import com.example.oracleapi.service.field.FieldService
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource


@Component
class OrderHeadStatusGet(
    dataSource: DataSource,
    private val fieldService: FieldService
) : BasePackage(dataSource) {

    override val pkg = "PKG_ORDERHEAD"
    override val method = "Order_Status"
    override val count = 1


    fun take(rn: Long): OrderHeadStatusResponse {
        return dataSource.executeFun {
            it.registerOutParameter(1,Types.INTEGER)
            it.setLong(2,rn)
            it.execute()
            val status = it.getLong(1)
            val field = fieldService.getFieldValue(Field.ORDER_STATUS, status)
            OrderHeadStatusResponse(
                rn,
                status,
                field
            )
        }

    }
}