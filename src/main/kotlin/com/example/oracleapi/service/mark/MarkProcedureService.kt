package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис для всех процедур пакета PKG_MARK
 * Контроллер обращается только к этому сервису
 */
@Service
class MarkProcedureService(
    private val markViewService: MarkViewService
) {
    @Transactional(readOnly = true)
    fun find(request: MarkFindRequest): MarkFindResponse =
        markViewService.findByKm(request)
}