package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.tohead.ToheadDto
import com.example.oracleapi.service.tohead.ToheadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/tohead")
@Tag(name = "Исходящие контракты")
class ToHeadController(
    private val toheadService: ToheadService
) : BaseController() {

    @GetMapping("/get_by_rn/{rn}")
    @Operation(summary = "Получить все склады")
    fun getByRN(@PathVariable rn: Long): MyApiResponse<ToheadDto> {
        return success(toheadService.toheadFindByRn(rn))
    }

}