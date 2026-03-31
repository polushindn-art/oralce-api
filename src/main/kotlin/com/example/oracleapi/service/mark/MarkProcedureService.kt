package com.example.oracleapi.service.mark

import com.example.oracleapi.common.GeneralResponse
import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import org.springframework.stereotype.Service

/**
 * Сервис для всех процедур пакета PKG_MARK
 * Контроллер обращается только к этому сервису
 */
@Service
class MarkProcedureService(
    private val markUpdProcedure: MarkUpdProcedure,
    private val markViewService: MarkViewService
) {
    fun upd(request: MarkUpdRequest): GeneralResponse<MarkUpdResponse> =
        markUpdProcedure.execute(request)

    fun find(request: MarkFindRequest): GeneralResponse<MarkFindResponse> =
        markViewService.findByKm(request)
}