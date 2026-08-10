package com.example.oracleapi.repository.stock

import com.example.oracleapi.entity.table.Stock
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal

interface StockRepository : JpaRepository<Stock, Long> {
    fun findStocksByNomen(nomen: BigDecimal): List<Stock>
}