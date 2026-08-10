package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.stock.StockDto
import com.example.oracleapi.service.stock.StockService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/v1/stock")
@Tag(name = "Остаток на складах")
class StockController(
    val stockService: StockService
) : BaseController() {

    @GetMapping("/findByNomen")
    @Operation(summary = "Получить остаток номенклатуры на складах")
    fun getByNomen(
        @Valid @RequestParam nomen: BigDecimal
    ): MyApiResponse<List<StockDto>> {
        return successList(stockService.findStocksByNomen(nomen))
    }


}