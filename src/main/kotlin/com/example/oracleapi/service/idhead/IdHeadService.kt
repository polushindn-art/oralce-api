package com.example.oracleapi.service.idhead

import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.dto.idhead.prihod.PrihodRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import org.springframework.stereotype.Service

@Service
class IdHeadService(
    val prihodFunction: PrihodCreate,
    val updateStatusFun: UpdateStatus,
    val deleteFun: IdHeadDelete
) {
    fun prihodCreate(request: PrihodRequest): Long {
        return prihodFunction.createPrihodByJson(request)
    }

    fun updateStatus(request: StatusUpdateRequest) {
        return updateStatusFun.updateStatus(request)
    }

    fun delete(request: IdHeadDeleteRequest) {
        return deleteFun.delete(request)
    }

}