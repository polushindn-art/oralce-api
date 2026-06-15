package com.example.oracleapi.service.agnList

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.ResponseRN
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class AgnListDel(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = AGNLIST
    override val method = "del"
    override val count = 1

    fun take(rn: Long): ResponseRN {
        return dataSource.executePrc {
            it.setLong(1, rn)
            it.execute()
            ResponseRN(
                rn
            )
        }
    }

}