package com.example.oracleapi.service.public

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.common.BasePkg
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import com.example.oracleapi.util.BarcodeUtils
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.sql.Types
import javax.sql.DataSource

@Component
class PublicGetNomenByBarcodeProcedure(
    dataSource: DataSource
) : BasePackage(dataSource) {

    override val pkg = PUBLIC
    override val method = "GETNOMENBYBARCODE"
    override val count = 1

    fun getNomenByBarcodeProcedure(barcode: String): GetNomenByBarcodeResponse {
        val cleaned = BarcodeUtils.cleanBarcodeText(barcode)
        return dataSource.executeFun {
            it.registerOutParameter(1, Types.NUMERIC)
            it.setString(2, cleaned)
            it.execute()
            GetNomenByBarcodeResponse(
                it.getLong(1),
                 barcode
            )
        }

    }

}