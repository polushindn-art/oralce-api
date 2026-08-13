package com.example.oracleapi.service.stock

import com.example.oracleapi.config.StoreNameMapper
import com.example.oracleapi.dto.nomnlist.NomnlistDto
import com.example.oracleapi.dto.stock.StockInfoDto
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StockMessageFormatter(
    private val storeNameMapper: StoreNameMapper
) {

    /**
     * Полное сообщение с остатками и именем товара
     * Если товар не найден — возвращает сообщение об ошибке
     */
    fun formatStockFull(
        nomen: NomnlistDto?,           // ← nullable
        stocks: List<StockInfoDto>,
        barcode: String? = null
    ): String {
        // Если товар не найден
        if (nomen == null || nomen.rn == null) {
            return buildString {
                appendLine("❌ *Товар не найден*")
                appendLine()
                if (barcode != null) {
                    appendLine("Штрих-код: `$barcode`")
                    appendLine()
                }
                appendLine("Возможные причины:")
                appendLine("• Товар не найден в базе")
                appendLine("• Штрих-код введен неверно")
            }
        }

        // Только склады с наличием
        val available = stocks.filter { it.quantToSale != null && it.quantToSale > BigDecimal.ZERO }

        // Если товар есть, но на складах нет
        if (available.isEmpty()) {
            return buildString {
                appendLine("📦 *Товар найден, но отсутствует на складах*")
                appendLine()
                appendLine("📌 *${nomen.nomenname ?: "Без названия"}*")
                appendLine("🔢 Артикул: ${nomen.article ?: "не указан"}")
                if (barcode != null) {
                    appendLine("📋 Штрих-код: `$barcode`")
                }
                appendLine()
                appendLine("Товар временно отсутствует.")
                appendLine("Обратитесь к менеджеру для уточнения.")
            }
        }

        // Есть остатки — показываем
        return buildString {
            appendLine("📦 *Остатки товара*")
            appendLine()
            appendLine("📌 *${nomen.nomenname ?: "Без названия"}*")
            appendLine("🔢 Артикул: ${nomen.article ?: "не указан"}")
            if (barcode != null) {
                appendLine("📋 Штрих-код: `$barcode`")
            }
            appendLine()

            available.forEach { stock ->
                val qty = stock.quantToSale ?: BigDecimal.ZERO
                val statusEmoji = when {
                    qty > BigDecimal.TEN -> "🟢"
                    qty > BigDecimal.ZERO -> "🟡"
                    else -> "🔴"
                }
                val fullName = storeNameMapper.getDisplayName(stock.pbeCode)
                val emoji = storeNameMapper.getEmoji(stock.pbeCode)

                appendLine("$statusEmoji $emoji *$fullName*")
                appendLine("   📦 Остаток: *$qty шт*")

                stock.price?.let {
                    appendLine("   💰 Цена: *$it ₽*")
                }

                stock.priceCard?.let {
                    if (it != stock.price) {
                        appendLine("   💳 По карте: *$it ₽*")
                    }
                }
                appendLine()
            }

            val total = available.sumOf { it.quantToSale ?: BigDecimal.ZERO }
            appendLine("---")
            appendLine("📊 *Итого: $total шт*")
        }
    }
}