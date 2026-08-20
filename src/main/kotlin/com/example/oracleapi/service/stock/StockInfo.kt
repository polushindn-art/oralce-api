package com.example.oracleapi.service.stock

import com.example.oracleapi.config.StoreNameMapper
import com.example.oracleapi.dto.stock.StockInfoDto
import com.example.oracleapi.repository.stock.StockRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StockInfo(
    private val stockRepository: StockRepository,
    private val storeNameMapper: StoreNameMapper
) {
    fun getStockInfo(nomen: BigDecimal): List<StockInfoDto> {
        return stockRepository.findStockInfo(nomen).map {
            StockInfoDto(
                nomenName = it.getNomenName(),
                storeCode = it.getStoreCode(),
                storeDisplayName = storeNameMapper.getDisplayName(it.getPbeCode()),
                storeShortName = storeNameMapper.getShortName(it.getPbeCode()),
                storeEmoji = storeNameMapper.getEmoji(it.getPbeCode()),
                nomen = it.getNomenId()?.toLong(),
                store = it.getStoreId()?.toLong(),
                quantToSale = it.getQuantToSale(),
                storePbe = it.getStorePbe(),
                pbeCode = it.getPbeCode(),
                price = it.getPrice(),
                priceCard = it.getPriceCard(),
                address = it.getAddress(),
                webStore = it.getWebStore(),
                meascode = it.getMeasCode()
            )
        }
    }
}