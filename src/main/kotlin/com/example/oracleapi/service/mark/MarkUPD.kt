package com.example.oracleapi.service.mark

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class MarkUPD(
    dataSource: DataSource,
    val objectMapper: ObjectMapper
) : BasePackage(dataSource) {

    override val pkg = "PKG_MARK"
    override val method = "UPD"
    override val count = 6

    fun take(request: MarkUpdRequest): MarkUpdResponse {
        val jsonString = objectMapper.writeValueAsString(request.json)
        return dataSource.executeFun {
            it.registerOutParameter(1,Types.DOUBLE)
            it.setString(2,request.km)
            it.setString(3,jsonString)
            it.setString(4,request.table)
            if (request.tablern != null) {
                it.setLong(5,request.tablern)
            } else {
                it.setNull(5,Types.NULL)
            }
            it.setInt(6,request.status)
            it.setString(7,request.note)
            it.execute()
            MarkUpdResponse(
                it.getLong(1),
                request.km
            )
        }
    }

}