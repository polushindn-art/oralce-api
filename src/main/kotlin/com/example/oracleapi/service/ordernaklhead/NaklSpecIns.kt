package com.example.oracleapi.service.ordernaklhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.orderNakl.OrderNaklSpecRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklSpecResponse
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class NaklSpecIns(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = "PKG_ORDERNAKLSPEC"
    override val method = "ins"
    override val count = 15

    fun take(request: OrderNaklSpecRequest): OrderNaklSpecResponse {
        return dataSource.executePrc {

            with(request) {
                it.setLong(1, prn)
                it.setLong(2, nomen)
                it.setBigDecimal(3, inprice)
                it.setBigDecimal(4, quant)
                it.setBigDecimal(5, summ)
                if (measalt != null) {
                    it.setLong(6, measalt)
                } else {
                    it.setNull(6, Types.NUMERIC)
                }
                if (quantalt != null) {
                    it.setBigDecimal(7, quantalt)
                } else {
                    it.setNull(7, Types.NUMERIC)
                }
                it.setBigDecimal(8,ndsrate)
                it.setLong(9, country)
                it.setString(10,gtd)
                it.registerOutParameter(11, Types.VARCHAR)
                it.registerOutParameter(12, Types.NUMERIC)
                it.setBoolean(13, isUpdate)
                it.setBoolean(14, isTrans)
                it.setInt(15, changeOverHead)
            }
            it.execute()

            val tideCheck = it.getString(11) ?: "-"
            val rn = it.getLong(12)

            OrderNaklSpecResponse(
                rn,
                tideCheck,
            )
        }
    }

}