package com.example.oracleapi.util

object BarcodeUtils {
    fun cleanBarcodeText(text: String): String {
        return text.filter { it.code in 32..126 }
    }
}