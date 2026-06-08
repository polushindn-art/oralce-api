package com.example.oracleapi.service.ordernaklhead

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadRequest
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class NaklHeadIns(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = "PKG_ORDERNAKLHEAD"
    override val method = "ins"
    override val count = 9

    fun take(request: OrderNaklHeadRequest): ResponseRN {
        return dataSource.executePrc {

            it.setLong(1,request.prn)

            it.setLong(2,request.provider)

            if (request.basisdoctype != null) {
                it.setLong(3, request.basisdoctype)
            } else {
                it.setNull(3, Types.NUMERIC)
            }

            it.setString(4, request.basisdocpref)

            if (request.basisdocnumb != null) {
                it.setLong(5, request.basisdocnumb)
            } else {
                it.setNull(5, Types.NUMERIC)
            }

            if (request.basisdocdate != null) {
                it.setDate(6, java.sql.Date.valueOf(request.basisdocdate))
            } else {
                it.setNull(6, Types.DATE)
            }

            if (request.numbttn != null) {
                it.setLong(7, request.numbttn)
            } else {
                it.setNull(7, Types.NUMERIC)
            }

            it.registerOutParameter(8, Types.NUMERIC)

            it.setBoolean(9, request.isUpdate)

            it.execute()

            val rn = it.getLong(8)

            ResponseRN(
                rn
            )
        }
    }

}