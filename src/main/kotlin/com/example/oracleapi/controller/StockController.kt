package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.stock.StockDto
import com.example.oracleapi.dto.stock.StockInfoDto
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
    private val stockService: StockService
) : BaseController() {

    @GetMapping("/findByNomen")
    @Operation(summary = "Получить остаток номенклатуры на складах")
    fun getByNomen(
        @Valid @RequestParam nomen: BigDecimal
    ): MyApiResponse<List<StockDto>> {
        return successList(stockService.findStocksByNomen(nomen))
    }

    @GetMapping("/findByNomenNotNull")
    @Operation(summary = "Получить остаток номенклатуры на складах больше 0")
    fun getByNomenNotNull(
        @Valid @RequestParam nomen: BigDecimal
    ): MyApiResponse<List<StockDto>> {
        return successList(stockService.findStocksByNomenQuantNotNull(nomen))
    }

    @GetMapping("/info")
    @Operation(
        summary = "Получить информацию о товаре",
        description = "Возвращает информацию о товаре"
    )
    fun getStockInfo(
        @RequestParam nomen: BigDecimal
    ): MyApiResponse<List<StockInfoDto>> {
        return successList(stockService.getStockByNomenInfo(nomen))
    }

}