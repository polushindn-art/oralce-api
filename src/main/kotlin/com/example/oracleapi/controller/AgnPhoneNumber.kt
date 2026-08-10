package com.example.oracleapi.controller

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.agnphonenumber.AgnPhoneService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/agnphone")
@Tag(name = "Номера контрагентов")
class AgnPhoneNumber(
    private val agnPhoneService: AgnPhoneService
) : BaseController() {
    @GetMapping("/get")
    @Operation(
        description = "Возвращает запись из таблицы AgnList по RN",
        summary = "Получить контрагента"
    )
    fun findPhone(@Valid phone: String): MyApiResponse<List<AgnphonenumberlistDto>> {
        return successList(agnPhoneService.searchByPhone(phone))
    }

    @GetMapping("/exist")
    @Operation(
        description = "Проверить что клиент зарегистрирован в Боте",
        summary = "Проверить существование"
    )
    fun existsByPhone(@Valid phone: String): MyApiResponse<Boolean> {
        return success(agnPhoneService.existsByPhone(phone))
    }
}