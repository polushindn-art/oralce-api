package com.example.oracleapi.service.prcDoc

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.prcDoc.head.PrcDocHeadDelRequest
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class PrcDocHeadDel(dataSource: DataSource) : BasePackage(dataSource) {

    override val pkg = PRCDOCHEAD
    override val method = "del"
    override val count = 2

    fun take(request: PrcDocHeadDelRequest): RnResponse {
        return dataSource.executePrc {

            it.setLong(1, request.rn)
            it.setBoolean(2, request.isUpdate)

            it.execute()

            RnResponse(
                request.rn
            )
        }
    }

}