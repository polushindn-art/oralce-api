package com.example.oracleapi.service.price

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.price.RequestPriceDto
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class GetPriceOut(
    dataSource: DataSource
) : BasePackage(dataSource) {
    override val pkg = PRICE
    override val method = "getPriceOut"
    override val count = 3

    fun take(request: RequestPriceDto): Float {
        return dataSource.executeFun {
            it.registerOutParameter(1, Types.FLOAT)
            it.setLong(2, request.nomen)
            it.setLong(3, request.store)
            it.setInt(4, request.typeOut)
            it.execute()
            it.getFloat(1)
        }
    }

}