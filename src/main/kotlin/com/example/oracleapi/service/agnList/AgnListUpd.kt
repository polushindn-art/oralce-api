package com.example.oracleapi.service.agnList

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.agn.AgnListUpdRequest
import com.example.oracleapi.dto.agn.AgnListUpdResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class AgnListUpd(
    private val objectMapper: ObjectMapper,
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = null
    override val method = "AGNLIST_UPD_JSON"
    override val count = 1

    fun take(request: AgnListUpdRequest): AgnListUpdResponse {
        val json = objectMapper.writeValueAsString(request)

        return dataSource.executePrc { stmt ->
            stmt.setString(1, json)
            stmt.execute()

            AgnListUpdResponse(rn = request.rn)
        }
    }

}