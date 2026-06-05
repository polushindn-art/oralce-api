package com.example.oracleapi.service.prcDoc

import com.example.oracleapi.common.BasePackage
import com.example.oracleapi.dto.prcDoc.head.PrcdocheadInsRequest
import com.example.oracleapi.dto.prcDoc.head.PrcdocheadInsResponse
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.sql.Types
import javax.sql.DataSource

@Component
class PrcDocHeadIns(dataSource: DataSource ) : BasePackage(dataSource) {

    override val pkg = "PKG_PRCDOCHEAD"
    override val method = "ins"
    override val count = 12

    fun take(request: PrcdocheadInsRequest): PrcdocheadInsResponse {
        return dataSource.executePrc {
            var idx = 1

            it.setLong(idx++, request.crn ?: 0)
            it.setLong(idx++, request.doctype)
            it.setString(idx++, request.docpref)

            val idxDoc = idx++
            if (request.docnumb != null) {
                it.setLong(idxDoc, request.docnumb)     // IN
            } else {
                it.setNull(idxDoc, Types.DOUBLE)
            }
            it.registerOutParameter(idxDoc, Types.DOUBLE)  // docnumb_

            it.setDate(idx++, java.sql.Date.valueOf(request.docdate))

            if (request.orderhead != null) {
                it.setLong(idx++, request.orderhead)
            } else {
                it.setNull(idx++, Types.DOUBLE)
            }

            it.setString(idx++, request.note)

            val idxRn = idx++
            if (request.rn != null) {
                it.setLong(idxRn, request.rn)           // IN
            } else {
                it.setNull(idxRn, Types.NUMERIC)
            }
            it.registerOutParameter(idxRn, Types.NUMERIC)  // rn_

            it.setString(idx++, request.checkRoznPrice)

            val idxCur = idx++
            it.registerOutParameter(idxCur, Types.REF_CURSOR) // cur

            it.setBoolean(idx++, request.isCheckOnly)
            it.setBoolean(idx++, request.isUpdate)


            it.execute()

            val curData = mutableListOf<Map<String, Any?>>()
            (it.getObject(idxCur) as? ResultSet)?.use { rs ->
                val meta = rs.metaData
                while (rs.next()) {
                    val row = (1..meta.columnCount).associate { i ->
                        meta.getColumnName(i) to rs.getObject(i)
                    }
                    curData.add(row)
                }
            }

            PrcdocheadInsResponse(
                rn = it.getLong(idxRn),
                docnumb = it.getLong(idxDoc),
                cur = curData
            )

        }
    }

}