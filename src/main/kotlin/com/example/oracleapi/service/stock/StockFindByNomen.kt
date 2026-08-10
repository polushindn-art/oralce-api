package com.example.oracleapi.service.stock

import com.example.oracleapi.dto.stock.StockDto
import com.example.oracleapi.repository.stock.StockRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StockFindByNomen(
    private val stockRepository: StockRepository
) {
    fun take(nomen: BigDecimal): List<StockDto> {
        return stockRepository.findStocksByNomen(nomen).map { StockDto.fromEntity(it) }
    }
}