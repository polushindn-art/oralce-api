package com.example.oracleapi.repository.price

import com.example.oracleapi.entity.table.Price
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal

interface PriceRepository : JpaRepository<Price, Long> {
    fun findPriceByNomen(nomen: BigDecimal): List<Price>?
}