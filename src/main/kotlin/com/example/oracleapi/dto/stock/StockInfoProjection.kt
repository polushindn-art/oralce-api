package com.example.oracleapi.dto.stock

import java.math.BigDecimal

interface StockInfoProjection {
    fun getNomenName(): String?
    fun getStoreCode(): String?
    fun getNomenId(): BigDecimal?
    fun getStoreId(): BigDecimal?
    fun getQuantToSale(): BigDecimal?
    fun getStorePbe(): Long?
    fun getPbeCode(): String?
    fun getPrice(): BigDecimal?
    fun getPriceCard(): BigDecimal?
    fun getAddress(): String?
    fun getWebStore(): Int?
}