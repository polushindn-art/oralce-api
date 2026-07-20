package com.example.oracleapi.service.public

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.public.GenIdResponse
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class PublicGenIdRnProcedur(
    dataSource: DataSource,
) : BasePackage(
    dataSource
) {

    override val pkg = PUBLIC
    override val method = "GenIDRN"
    override val count = 0

    /**
     * Генерирует новый уникальный идентификатор RN
     * @return ProcedureResult с сгенерированным ID
     */

    fun take(): GenIdResponse {
        return dataSource.executeFun {
            it.registerOutParameter(1, Types.NUMERIC)
            it.execute()
            GenIdResponse.single(
                it.getLong(1)
            )
        }
    }
}