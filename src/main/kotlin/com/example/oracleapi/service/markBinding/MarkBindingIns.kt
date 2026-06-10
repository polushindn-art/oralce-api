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
    override val count = 4

    fun take(request: MarkBindingRequest): MarkBindingResponse {
        return dataSource.executePrc {
            with(it) {
                registerOutParameter(1,Types.NUMERIC)
                setLong(2,request.prn)
                setLong(3,request.docRn)
                setString(4,request.docTableName)
                execute()
            }

            val rn = it.getLong(1)

            MarkBindingResponse(
                rn,
                request.prn,
                request.docRn,
                request.docTableName,
                LocalDateTime.now(),

            )
        }
    }

}