package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.acatalog.AcatalogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/acatalog")
@Tag(name = "Каталоги иерархии разделов системы")
class AcatralogController(
    private val aCatalogService: AcatalogService
) : BaseController() {
    @GetMapping("/getCatalogByNomen")
    @Operation(summary = "Получить RN каталога для заказа по rn номенклатруры")
    fun getCatalogByNomen(
        @Valid @RequestParam nomen: Long
    ): MyApiResponse<ResponseRN> {
        return success(aCatalogService.getCatalogByNomenForOrder(nomen))
    }
}