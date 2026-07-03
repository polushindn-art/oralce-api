package com.example.oracleapi.service.markBinding

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.markBinding.MarkBindingRequest
import com.example.oracleapi.dto.markBinding.MarkBindingResponse
import org.springframework.stereotype.Component
import java.sql.Types
import java.time.LocalDateTime
import javax.sql.DataSource

@Component
class MarkBindingIns(
    dataSource: DataSource
) : BasePackage(dataSource) {
    override val pkg = MARK_BINDING
    override val method = "ins"
    override val count = 6

    fun take(request: MarkBindingRequest): MarkBindingResponse {
        return dataSource.executePrc {
            with(it) {
                registerOutParameter(1,Types.NUMERIC)
                setString(2,request.km)
                setLong(3,request.specRn)
                setString(4,request.docTableName)
                setInt(5,request.status)
                setString(6,request.note)
                execute()
            }

            val rn = it.getLong(1)

            MarkBindingResponse(
                rn,
                request.km,
                request.specRn,
                request.docTableName,
                LocalDateTime.now(),

            )
        }
    }

}