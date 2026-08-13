package com.example.oracleapi.service.stock

import com.example.oracleapi.dto.stock.StockDto
import com.example.oracleapi.dto.stock.StockInfoDto
import com.example.oracleapi.service.nomnlist.NomnlistService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class StockService(
    private val stockFindByNomen: StockFindByNomen,
    private val stockInfoPipelineGet: StockInfo,
    private val stockMessageFormatter: StockMessageFormatter,
    private val nomnlistService: NomnlistService
) {
    fun findStocksByNomen(nomen: BigDecimal): List<StockDto> {
        return stockFindByNomen.all(nomen)
    }

    fun findStocksByNomenQuantNotNull(nomen: BigDecimal): List<StockDto> {
        return stockFindByNomen.notNull(nomen)
    }

    fun getStockByNomenInfo(nomen: BigDecimal): List<StockInfoDto> {
        return stockInfoPipelineGet.getStockInfo(nomen)
    }

    fun getFullStockMessageByBarcode(barcode: String): String {
        val nomen = nomnlistService.findByBarcode(barcode)
        val nomenId = if (nomen?.rn != null) BigDecimal.valueOf(nomen.rn) else null
        val stocks = if (nomenId != null) stockInfoPipelineGet.getStockInfo(nomenId).filter { it.webStore == 1 } else emptyList()
        return stockMessageFormatter.formatStockFull(nomen, stocks, barcode)
    }

}