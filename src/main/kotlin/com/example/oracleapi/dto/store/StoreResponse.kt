package com.example.oracleapi.dto.store

import com.fasterxml.jackson.annotation.JsonInclude
import com.example.oracleapi.entity.table.Store
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StoreResponse(
    val rn: Long,
    val storecode: String,
    val storename: String,
    val storenumb: String?,
    val storepbe: Long,
    val note: String?,
    val workindex: Long?,
    val reserve: Long?,
    val colindex: Long?,
    val prLogist: String?,
    val autoz: Long?,
    val s: Long?,
    val storeoc: Boolean?,
    val qmax: BigDecimal?,
    val qmaxcur: BigDecimal?,
    val qcur: BigDecimal?,
    val qcur0: BigDecimal?,
    val vmax: BigDecimal?,
    val vcur: BigDecimal?,
    val vcur0: BigDecimal?,
    val usesas: Long?,
    val equeue: Long?,
    val storerazborRn: Long?,
    val cannegative: Long?,
    val eschema: Long?,
    val canbasket: Long?,
    val isvisible: Boolean?,
    val webstore: Boolean?
) {
    companion object {
        fun fromEntity(store: Store): StoreResponse {
            return StoreResponse(
                rn = store.rn,
                storecode = store.storecode,
                storename = store.storename,
                storenumb = store.storenumb,
                storepbe = store.storepbe,
                note = store.note,
                workindex = store.workindex,
                reserve = store.reserve,
                colindex = store.colindex,
                prLogist = store.prLogist,
                autoz = store.autoz,
                s = store.s,
                storeoc = store.storeoc,
                qmax = store.qmax,
                qmaxcur = store.qmaxcur,
                qcur = store.qcur,
                qcur0 = store.qcur0,
                vmax = store.vmax,
                vcur = store.vcur,
                vcur0 = store.vcur0,
                usesas = store.usesas,
                equeue = store.equeue,
                storerazborRn = store.storerazbor?.rn,
                cannegative = store.cannegative,
                eschema = store.eschema,
                canbasket = store.canbasket,
                isvisible = store.isvisible,
                webstore = store.webstore
            )
        }
    }
}

// Упрощенный вариант для ТСД (только нужные поля)
data class StoreSimpleResponse(
    val rn: Long,
    val storecode: String,
    val storename: String,
    val eschema: Long? = null,
    val usesas: Long? = null,
    val note: String?
) {
    companion object {
        fun fromEntity(store: Store): StoreSimpleResponse {
            return StoreSimpleResponse(
                rn = store.rn,
                storecode = store.storecode,
                storename = store.storename,
                eschema = store.eschema,
                usesas = store.usesas,
                note = store.note
            )
        }

    }
}