package com.example.oracleapi.service.prcDoc

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.prcDoc.spec.PrcDocSpecDelRequest
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class PrcDocSpecDel(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = PRCDOCSPEC
    override val method = "del"
    override val count = 2

    fun take(request: PrcDocSpecDelRequest): ResponseRN {
        return dataSource.executePrc {
            it.setLong(1, request.rn)
            it.setBoolean(2, request.isUpdate)
            it.execute()
            ResponseRN(request.rn)
        }
    }

}