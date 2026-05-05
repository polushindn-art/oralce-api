package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.tsdlist.Registeredjson
import com.example.oracleapi.dto.tsdlist.TsdUpsertRequest
import com.example.oracleapi.dto.tsdlist.TsdUpsertResponse
import com.example.oracleapi.dto.tsdlist.UsedJson
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tsdlist")
@Tag(name = "tsdlist", description = "Процедуры пакета PKG TSDLIST")
class TsdListController(
    private val tsdListService: TsdListService,
) {
    @GetMapping("/registeredjson")
    @Operation(
        summary = "pkg tsdlist.registeredjson",
        description = "Получить список зарегистрированных ТСД"
    )
    fun registeredjson(
        @RequestParam(required = false) sn: String?,
        request: HttpServletRequest
    ): MyApiResponse<List<Registeredjson>> {
        val result = tsdListService.getRegisteredSessions(sn)
        return MyApiResponse.success(
            data = result,
            "Список получен",
            path = request.requestURI
        )
    }

    @GetMapping("/usedtsd")
    @Operation(
        summary = "usedtsd",
        description = "Получить список активных пользователй ТСД"
    )
    fun usedTsd(
        @RequestParam(required = false) pbe: Long?,
        request: HttpServletRequest
    ): MyApiResponse<List<UsedJson>> {
        val data = tsdListService.getUsedTsd(pbe)
        return MyApiResponse.success(
            data = data,
            message = "Список активных пользователей ТСД успешно получен",
            path = request.requestURI
        )
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
        @Valid @RequestBody request: TsdUpsertRequest,
        httpRequest: HttpServletRequest
    ): MyApiResponse<TsdUpsertResponse> {
        val result = tsdListService.upsertTerminal(request)
        val message = if (result.isNew) {
            "Терминал успешно создан. Device ID: ${result.deviceId}, RFID: ${result.generatedRfid}"
        } else {
            "Терминал успешно обновлен. Device ID: ${result.deviceId}"
        }

        val response = MyApiResponse.success(
            data = result,
            message = message,
            path = httpRequest.requestURI
        )

        return response
    }

}