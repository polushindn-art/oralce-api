package com.example.oracleapi.service.mark

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.mark.ParseMarkResponse
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class ParseMark(
    dataSource: DataSource,
) : BasePackage(
    dataSource
) {

    override val pkg = null
    override val method = "PARSE_MARKING_CODE"
    override val count = 4

    fun take(km: String): ParseMarkResponse {
        return dataSource.executePrc {
            it.setString(1,km)
            it.registerOutParameter(2, Types.VARCHAR,4000)
            it.registerOutParameter(3, Types.VARCHAR,4000)
            it.registerOutParameter(4, Types.VARCHAR,4000)
            it.execute()
            ParseMarkResponse(
                km,
                it.getString(2),
                it.getString(3),
                it.getString(4),
            )
        }
    }

}