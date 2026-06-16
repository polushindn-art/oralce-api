package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.prcDoc.head.PrcDocHeadDelRequest
import com.example.oracleapi.dto.prcDoc.head.PrcDocStatusRequest
import com.example.oracleapi.dto.prcDoc.head.PrcDocStatusResponse
import com.example.oracleapi.dto.prcDoc.head.PrcdocheadInsRequest
import com.example.oracleapi.dto.prcDoc.head.PrcdocheadInsResponse
import com.example.oracleapi.dto.prcDoc.spec.PrcDocSpecDelRequest
import com.example.oracleapi.dto.prcDoc.spec.PrcdocspecInsRequest
import com.example.oracleapi.service.prcDoc.PrcDocService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/prcdoc")
@Tag(name = "Документ изменения ТМЦ")
class PrcDocController(
    private val prcDocService: PrcDocService
) : BaseController() {

    @PostMapping("/ins_head")
    @Operation(summary = "Создать заголовок документа ТМЦ")
    fun insHead(@Valid @RequestBody request: PrcdocheadInsRequest): MyApiResponse<PrcdocheadInsResponse> {
        return success(prcDocService.prcDocIns(request))
    }

    @PostMapping("/ins_spec")
    @Operation(summary = "Создать спецификацию документа ТМЦ")
    fun insSpec(
        @Valid @RequestBody request: PrcdocspecInsRequest
    ): MyApiResponse<ResponseRN> {
        return success(prcDocService.prcSpecIns(request))
    }

    @PutMapping("/status_update")
    @Operation(summary = "Обновить статус документа")
    fun statusUpdate(
        @Valid @RequestBody request: PrcDocStatusRequest
    ): MyApiResponse<PrcDocStatusResponse> {
        return success(prcDocService.statusUpdate(request))
    }

    @DeleteMapping("/del_head")
    @Operation(
        description = "Удаляет запись в таблице PrcDocHead",
        summary = "Удалить заголовок"
    )
    fun delHead(@Valid @RequestBody request: PrcDocHeadDelRequest): MyApiResponse<ResponseRN> {
        return success(prcDocService.delHead(request))
    }

    @DeleteMapping("/del_spec")
    @Operation(
        description = "Удаляет запись в таблице PrcDocSpec",
        summary = "Удалить спецификацию"
    )
    fun delSpec(@Valid @RequestBody request: PrcDocSpecDelRequest): MyApiResponse<ResponseRN> {
        return success(prcDocService.delSpec(request))
    }

}