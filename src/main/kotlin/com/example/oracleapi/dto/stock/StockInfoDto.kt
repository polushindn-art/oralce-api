package com.example.oracleapi.dto.stock

import java.math.BigDecimal

data class StockInfoDto(
    val nomenName: String? = null,        // наименование товара
    val storeCode: String? = null,        // код склада
    val storeDisplayName: String? = null,
    val storeShortName: String? = null,
    val storeEmoji: String? = null,
    val nomen: Long? = null,            // ID товара
    val store: Long? = null,            // ID склада
    val quantToSale: BigDecimal? = null,  // количество для продажи
    val storePbe: Long? = null,           // PBE склада
    val pbeCode: String? = null,          // Код склада
    val price: BigDecimal? = null,        // цена
    val priceCard: BigDecimal? = null,      // цена по карте
    val address: String? = null,        // адрес
    val webStore: Int? = null,
)