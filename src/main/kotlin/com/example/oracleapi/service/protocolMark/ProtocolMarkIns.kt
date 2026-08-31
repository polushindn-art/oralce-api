package com.example.oracleapi.service.protocolMark

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.protocolMark.ProtocolMarkRequest
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class ProtocolMarkIns(
    dataSource: DataSource
): BasePackage(dataSource) {

    override val pkg = PROTOCOLMARK
    override val count = 8
    override val method = "ins"

    fun take(request: ProtocolMarkRequest) {
        dataSource.executePrc {
            with(request) {
                it.setLong(1, tableRn)
                it.setInt(2, action)
                it.setStringOrNull(3,before)
                it.setStringOrNull(4, after)
                it.setStringOrNull(5,userName)
                it.setStringOrNull(6,userIp)
                it.setStringOrNull(7,programm)
                it.setStringOrNull(8,note)
                it.execute()
            }
        }
    }

}