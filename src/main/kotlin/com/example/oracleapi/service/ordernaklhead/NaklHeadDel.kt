package com.example.oracleapi.service.ordernaklhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadDelRequest
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class NaklHeadDel(dataSource: DataSource) : BasePackage(
    dataSource
) {
    override val pkg = ORDERNAKLHEAD
    override val method = "del"
    override val count = 2

    fun take(request: OrderNaklHeadDelRequest): ResponseRN {
        return dataSource.executePrc {
            it.setLong(1,request.rn)
            it.setBoolean(2,request.isUpdate ?: false)

            it.execute()

            ResponseRN(
                request.rn
            )

        }
    }

}