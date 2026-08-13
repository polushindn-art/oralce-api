package com.example.oracleapi.service.stock

import com.example.oracleapi.dto.stock.StockDto
import com.example.oracleapi.dto.stock.StockInfoDto
import com.example.oracleapi.repository.stock.StockRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal
import org.springframework.transaction.annotation.Transactional

@Component
class StockFindByNomen(
    private val stockRepository: StockRepository
) {
    fun all(nomen: BigDecimal): List<StockDto> {
        return stockRepository.findStocksByNomen(nomen).map { StockDto.fromEntity(it) }
    }

    fun notNull(nomen: BigDecimal): List<StockDto> {
        return stockRepository.findStocksByNomenQuantNotNull(nomen).map { StockDto.fromEntity(it) }
    }

}