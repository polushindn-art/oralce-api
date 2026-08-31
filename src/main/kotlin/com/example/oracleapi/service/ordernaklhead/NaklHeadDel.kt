package com.example.oracleapi.service.ordernaklhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.RnResponse
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

    fun take(request: OrderNaklHeadDelRequest): RnResponse {
        return dataSource.executePrc {
            it.setLong(1,request.rn)
            it.setBoolean(2,request.isUpdate ?: false)

            it.execute()

            RnResponse(
                request.rn
            )

        }
    }

}