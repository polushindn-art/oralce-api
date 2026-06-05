package com.example.oracleapi.service.prcDoc

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.prcDoc.head.PrcDocStatusRequest
import com.example.oracleapi.dto.prcDoc.head.PrcDocStatusResponse
import com.example.oracleapi.entity.Field
import com.example.oracleapi.service.field.FieldService
import com.example.oracleapi.service.orderhead.OrderHeadStatusUpdate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.sql.Types
import javax.sql.DataSource
import kotlin.use

@Component
class PrcDocStatusUpdate(
    dataSource: DataSource,
    private val fieldService: FieldService
) : BasePackage(dataSource) {

    override val pkg = "PKG_PRCDOCHEAD"
    override val method = "STATUS_UPDATE"
    override val count = 3

    fun take(request: PrcDocStatusRequest): PrcDocStatusResponse {
        return dataSource.executePrc {

            it.setLong(1, request.rn)
            it.setLong(2, request.status)
            it.registerOutParameter(3, Types.REF_CURSOR)

            it.execute()

            val curData = mutableListOf<Map<String, Any?>>()
            (it.getObject(3) as? ResultSet)?.use { rs ->
                val meta = rs.metaData
                while (rs.next()) {
                    val row = (1..meta.columnCount).associate { i ->
                        meta.getColumnName(i) to rs.getObject(i)
                    }
                    curData.add(row)
                }
            }

            val field = fieldService.getFieldValue(Field.PRCDOC_STATUS, request.status)

            PrcDocStatusResponse(
                request.rn,
                request.status,
                field,
                curData
            )
        }
    }

}