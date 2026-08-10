package com.example.oracleapi.service.stock

import com.example.oracleapi.dto.stock.StockDto
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class StockService(
    private val stockFindByNomen: StockFindByNomen
) {
    fun findStocksByNomen(nomen: BigDecimal): List<StockDto> {
        return stockFindByNomen.take(nomen)
    }
}