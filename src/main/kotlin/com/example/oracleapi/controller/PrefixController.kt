package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.prefix.PrefixResponse
import com.example.oracleapi.service.prefix.PrefixService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/prefix")
@Tag(name = "Префиксы")
class PrefixController(
    private val prefixService: PrefixService
) : BaseController() {

    @GetMapping("/getAll")
    @Operation(summary = "Получить все префиксы")
    fun get(
        @PageableDefault(size = 20, sort = ["docpref"]) pageable: Pageable
    ): MyApiResponse<List<PrefixResponse>> {
        return success(prefixService.getAllPrefixses(pageable))
    }

    @GetMapping("/getByDivisionCode")
    @Operation(summary = "Получить все префиксы по разделу")
    fun getByDivisionCode(
        @RequestParam divisionCode: String,
        @PageableDefault(size = 20, sort = ["docpref"]) pageable: Pageable
    ): MyApiResponse<List<PrefixResponse>> {
        return success(prefixService.getByDivicionCode(pageable, divisionCode))
    }
}