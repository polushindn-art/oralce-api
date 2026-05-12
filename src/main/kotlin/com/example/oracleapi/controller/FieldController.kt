package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.field.FieldResponse
import com.example.oracleapi.service.field.FieldService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/fields")
@Tag(name = "Поля и их значения", description = "API для работы со справочником полей")
class FieldController(
    private val fieldService: FieldService
) : BaseController() {

    @GetMapping("/{fieldName}")
    @Operation(summary = "Получить все значения поля (без учета регистра)")
    fun getFieldValues(
        @PathVariable fieldName: String
    ): MyApiResponse<List<FieldResponse>> {
        return successList(fieldService.getFieldValues(fieldName))
    }

    @GetMapping("/{fieldName}/{fieldValue}")
    @Operation(summary = "Получить значение поля по коду (без учета регистра)")
    fun getFieldValue(
        @PathVariable fieldName: String,
        @PathVariable fieldValue: Long
    ): MyApiResponse<FieldResponse> {
        return success(fieldService.getFieldValue(fieldName, fieldValue))
    }

}