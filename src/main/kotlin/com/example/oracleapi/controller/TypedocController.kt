package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.typedoc.TypedocResponse
import com.example.oracleapi.service.typedoc.TypedocService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/typedoc")
@Tag(name = "typedoc", description = "Типы документов Oracle")
class TypedocController(
    private val typedocService: TypedocService
) : BaseController() {
    @GetMapping("/all")
    @Operation(summary = "Получить все типы документов")
    fun getAll(): MyApiResponse<List<TypedocResponse>> {
        return success(typedocService.getAllTypedocs())
    }

    @GetMapping("/byDivisionCode/{divisionCode}")
    fun getByDivisionCode(@PathVariable divisionCode: String): MyApiResponse<List<TypedocResponse>> {
        return success(typedocService.getTypeDocByDivisionCode(divisionCode))
    }

}