package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.agn.AgnListForUpdResponse
import com.example.oracleapi.dto.agn.AgnListInsResponse
import com.example.oracleapi.dto.agn.AgnListUpdRequest
import com.example.oracleapi.dto.agn.AgnListUpdResponse
import com.example.oracleapi.dto.agnlist.AgnListInsRequest
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.agnList.AgnListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/agnlist")
@Tag(name = "Контрагенты")
class AgnListController(
    private val agnListService: AgnListService
) : BaseController() {

    @PostMapping("/insert")
    @Operation(
        description = "Создает запись в таблице AgnList",
        summary = "Создать контрагента"
    )
    fun ins(
        @Valid @RequestBody request: AgnListInsRequest
    ): MyApiResponse<AgnListInsResponse> {
        return success(agnListService.ins(request))
    }

    @PutMapping("/update")
    @Operation(
        description = "Обновляет запись в таблице AgnList",
        summary = "Обновить контрагента"
    )
    fun upd(
        @Valid @RequestBody request: AgnListUpdRequest
    ): MyApiResponse<AgnListUpdResponse> {
        val response = agnListService.und(request)
        return success(response)
    }

    @GetMapping("/get")
    @Operation(
        description = "Возвращает запись из таблицы AgnList по RN",
        summary = "Получить контрагента"
    )
    fun getAgn(@Valid rn: Long): MyApiResponse<AgnListForUpdResponse> {
        return success(agnListService.getByRnForUpdate(rn))
    }

    @DeleteMapping("/del")
    @Operation(
        description = "Удаляет запись из таблицы AgnList",
        summary = "Удалить запись"
    )
    fun delete(@Valid rn: Long): MyApiResponse<ResponseRN> {
        return success(agnListService.del(rn))
    }

}