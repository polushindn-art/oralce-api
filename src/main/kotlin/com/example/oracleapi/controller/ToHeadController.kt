package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.tohead.ToheadDto
import com.example.oracleapi.service.tohead.ToheadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequestMapping("/v1/tohead")
@Tag(name = "Исходящие контракты")
class ToHeadController(
    private val toheadService: ToheadService
) : BaseController() {

    @GetMapping("/get_by_rn/{rn}")
    @Operation(summary = "Получить заголовок по RN")
    fun getByRN(@PathVariable rn: Long): MyApiResponse<ToheadDto> {
        return success(toheadService.toheadFindByRn(rn))
    }

    @GetMapping("filter/page")
    @Operation(summary = "Фильтр документов", description = "Фильтрация документов с пагинацией")
    fun getFilters(
        @RequestParam(required = false) doctype: BigDecimal?,
        @RequestParam(required = false) dateFrom: LocalDate?,
        @RequestParam(required = false) dateTo: LocalDate?,
        @ParameterObject
        @PageableDefault(size = 20, sort = ["docdate", "rn"], direction = Sort.Direction.DESC) pageable: Pageable
    ): MyApiResponse<List<ToheadDto>> {
        return success(toheadService.getFilterPage(doctype, dateFrom, dateTo, pageable))
    }

}