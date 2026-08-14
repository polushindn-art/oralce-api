package com.example.oracleapi.util

object Article {

    fun insertSpaceToArticle(article: String, separator: String = " "): String {
        // 1. Проверка на пустоту
        if (article.isEmpty()) return ""

        // 2. Если короче 13 символов — возвращаем как есть (или пустую строку?)
        if (article.length <= 13) return article

        // 3. Вставляем пробелы
        val sb = StringBuilder(article)
        sb.insert(3, separator)
        sb.insert(10, separator)

        // 4. Берём первые 13 символов (с пробелами)
        return sb.toString().take(13).trim()
    }

    fun shortArticle(article: String): String {
        if (article.isEmpty()) return ""
        if (article.length <= 11) return article
        return article.take(11).trim()
    }

}