package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.tsdlist.RegisteredjsonResponse
import com.example.oracleapi.dto.tsdlist.TsdIdResponse
import com.example.oracleapi.dto.tsdlist.TsdUpsertRequest
import com.example.oracleapi.dto.tsdlist.TsdUpsertResponse
import com.example.oracleapi.dto.tsdlist.UsedJsonResponse
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/tsdlist")
@Tag(name = "ТСД", description = "Терминал Сбора Данных")
class TsdListController(
    private val tsdListService: TsdListService,
) : BaseController() {
    @GetMapping("/registeredjson")
    @Operation(
        summary = "pkg tsdlist.registeredjson",
        description = "Получить список зарегистрированных ТСД"
    )
    fun registeredjson(
        @RequestParam(required = false) sn: String?
    ): MyApiResponse<List<RegisteredjsonResponse>> {
        return successList(tsdListService.getRegisteredSessions(sn))
    }

    @GetMapping("/usedtsd")
    @Operation(
        summary = "usedtsd",
        description = "Получить список активных пользователй ТСД"
    )
    fun usedTsd(
        @RequestParam(required = false) pbe: Long?
    ): MyApiResponse<List<UsedJsonResponse>> {
        return successList(tsdListService.getUsedTsd(pbe))
    }

    @PostMapping("/upsert")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Создать или обновить терминал по Device ID",
        description = """
            Поиск терминала осуществляется по Device ID (уникальный идентификатор устройства).
            
            - Если терминал с таким Device ID существует - обновляет переданные поля
            - Если нет - создает новый с автоматической генерацией RN и случайного RFID
            
            Обновляемые поля: sn, curversion, datestart, tsdprogram, tsdip, tsdname, 
            versioncode, pbe, note, tsdmac
            
            При создании нового терминала:
            - Если SN не передан - генерируется временный на основе Device ID
            - RFID генерируется случайный
        """
    )
    fun upsertTerminal(
        @Valid @RequestBody request: TsdUpsertRequest
    ): MyApiResponse<TsdUpsertResponse> {
        val result = tsdListService.upsertTerminal(request)
        return success(result, result.getSuccessMessage())
    }

    @GetMapping("/snByDeviceId/{deviceId}")
    fun getSnByDeviceId(
        @Valid @PathVariable deviceId: String
    ): MyApiResponse<TsdIdResponse> {
        return success(tsdListService.getSnByDeviceId(deviceId))
    }

}