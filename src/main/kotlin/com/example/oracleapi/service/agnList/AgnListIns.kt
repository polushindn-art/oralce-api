package com.example.oracleapi.service.agnList

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.agn.AgnListInsResponse
import com.example.oracleapi.dto.agnlist.AgnListInsRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class AgnListIns(
    private val objectMapper: ObjectMapper,
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = null
    override val method = "AGNLIST_INS_JSON"
    override val count = 2

    fun take(request: AgnListInsRequest): AgnListInsResponse {

        val json = objectMapper.writeValueAsString(request)

        return dataSource.executePrc {

            it.setString(1, json)
            it.registerOutParameter(2, Types.NUMERIC)
            it.execute()
            val rn = it.getLong(2)
            AgnListInsResponse(rn = rn)
        }
    }
}