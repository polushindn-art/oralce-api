package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.price.RequestPriceDto
import com.example.oracleapi.service.price.PriceService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/price")
@Tag(name = "Цена номенклатуры")
class PriceController(
    private val priceService: PriceService
) : BaseController() {
    @PostMapping("/getPriceOut")
    fun getPriceOut(@Valid @RequestBody request: RequestPriceDto): MyApiResponse<Float> {
        return success(priceService.getPriceOut(request))
    }
}