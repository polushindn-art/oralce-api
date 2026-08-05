package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.prcPriceOS.PrcPriceOSRequest
import com.example.oracleapi.service.prcPriceOS.PrcPriceOsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/prcprice")
@Tag(name = "Цены Отдела Отповых Продаж")
class PrcPriceOsController(
    private val prcPriceOsService: PrcPriceOsService
) : BaseController() {

    @PostMapping("/ins_api")
    @Operation(summary = "Создать цену")
    fun insApi(@Valid @RequestBody request: PrcPriceOSRequest): MyApiResponse<ResponseRN> {
        return success(prcPriceOsService.ins(request))
    }

}