package com.example.oracleapi.service.orderspec

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderspec.del.OrderSpecDelRequest
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class OrderSpecDel(dataSource: DataSource) : BasePackage(dataSource) {
    override val pkg = ORDERSPEC
    override val method = "del"
    override val count = 4

    fun delSpec(request: OrderSpecDelRequest): ResponseRN {
        return dataSource.executePrc {

            it.setLong(1, request.rn)
            it.setBoolean(2, request.isUpdate)
            it.setInt(3, request.changeOverHead)
            it.setBoolean(4, request.isSplit)

            it.execute()

            ResponseRN(
                rn = request.rn
            )
        }
    }

}