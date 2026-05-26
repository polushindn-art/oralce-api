package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.MarkFindRequest
import com.example.oracleapi.dto.mark.MarkFindResponse
import com.example.oracleapi.dto.mark.MarkUpdRequest
import com.example.oracleapi.dto.mark.MarkUpdResponse
import com.example.oracleapi.dto.mark.ParseMarkResponse
import org.springframework.stereotype.Service

/**
 * Сервис для всех процедур пакета PKG_MARK
 * Контроллер обращается только к этому сервису
 */
@Service
class MarkProcedureService(
    private val markUpdProcedure: MarkUpdProcedure,
    private val markViewService: MarkViewService,
    private val parseMark: ParseMark
) {
    fun upd(request: MarkUpdRequest): MarkUpdResponse =
        markUpdProcedure.execute(request)

    fun find(request: MarkFindRequest): MarkFindResponse =
        markViewService.findByKm(request)

    fun parseMark(km: String): ParseMarkResponse = parseMark.parseMarkCode(km)

    fun getNomenName(barcode: String): ParseMarkResponse = parseMark.getNomenName(barcode)
}