package com.example.oracleapi.service.acatalog

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.RequestRN
import com.example.oracleapi.dto.ResponseRN
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class GetCatalogByNomen(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = EMPTY
    override val method = "get_acatalog_rn"
    override val count = 1

    fun take(nomen: Long): ResponseRN {
        return dataSource.executeFun {
            it.registerOutParameter(1, Types.DOUBLE)
            it.setLong(2, nomen)
            it.execute()
            val rn = it.getLong(1)
            ResponseRN(rn)

        }
    }

}