package com.example.oracleapi.service.procedure

import com.example.oracleapi.CallBack
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class PkgMark(em: EntityManager) : CommonProcedure(em) {

    @Async
    @Transactional
    fun upd( inputData: DTO.UpdDTO ,finish: CallBack.Finish_) {
        val clob = convertToClob(inputData.json)

        try {
            // Вызываем процедуру Oracle
            val spq = em.createNamedStoredProcedureQuery("PKG_MARK.UPD")
            spq.setParameter("KM_", inputData.km)
            spq.setParameter("JSON_", clob)
            spq.setParameter("TABLE_", inputData.table)
            spq.setParameter("TABLERN_", inputData.tablern)
            spq.setParameter("STATUS_", inputData.status)
            spq.setParameter("NOTE_", inputData.note)
            spq.execute()
        } catch (e: Exception) {

        } finally {

        }
    }

    class DTO {
        data class UpdDTO(
            val km: String,
            val json: String,
            val table: String,
            val tablern: Number,
            val status: Int,
            val note: String? = null
        )
    }

}