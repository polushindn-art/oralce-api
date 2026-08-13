package com.example.oracleapi.config

import org.springframework.stereotype.Component

@Component
class StoreNameMapper {
    private val storeMap = mapOf(
        "АРСЕНАЛ-4" to "Арсенал Красноармейский",
        "АРСЕНАЛ-5" to "Арсенал Новосибирск",
        "АРСЕНАЛ-1" to "Арсенал Ленина",
        "АРСЕНАЛ-2" to "Арсенал Павловский",
        "АРСЕНАЛ-3" to "Арсенал Новоалтайск",
        "ЦС" to "Интернет-магазин"
    )

    fun getDisplayName(storeCode: String?): String {
        return storeMap[storeCode] ?: storeCode ?: "Неизвестный склад"
    }

    fun getShortName(storeCode: String?): String {
        if (storeCode == null) return "Неизвестный"
        val fullName = storeMap[storeCode] ?: return storeCode
        return fullName.replace("Арсенал ", "")
    }

    fun getEmoji(storeCode: String?): String {
        if (storeCode == null) return "🏢"
        return when {
            storeCode.contains("МАГАЗИН") -> "🏪"
            storeCode.contains("СКЛАД") -> "📦"
            else -> "🏢"
        }
    }
}