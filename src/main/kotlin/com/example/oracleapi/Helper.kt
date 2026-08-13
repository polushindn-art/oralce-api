package com.example.oracleapi

import kotlin.random.Random

class Helper {
    companion object {
        const val SCHEME = "Qreal"

        const val IDSTATUS = "ID_STATUS"

        fun currentTimestamp(): String {
            return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

        /**
         * Парсит Oracle JDBC URL и возвращает хост, порт и имя базы данных
         */
        fun parseOracleJdbcUrl(jdbcUrl: String): Triple<String, String, String> {
            val pattern = Regex("jdbc:oracle:thin:@(?://)?([^:/]+):(\\d+)[:/]([^?\\s]+)")
            val matchResult = pattern.find(jdbcUrl)

            return if (matchResult != null) {
                Triple(
                    matchResult.groupValues[1],  // host
                    matchResult.groupValues[2],  // port
                    matchResult.groupValues[3]   // database name
                )
            } else {
                Triple("unknown", "unknown", "unknown")
            }
        }

        /**
         * Генерирует случайный RFID в формате:
         * Префикс "RFID" + 12 случайных шестнадцатеричных символов
         * Пример: "RFID3F8A2B4C6D9E"
         */
        fun generateRandomRfid(): String {
            val hexChars = "0123456789ABCDEF"
            val randomHex = (1..12).map { hexChars[Random.nextInt(hexChars.length)] }.joinToString("")
            return "RFID$randomHex"
        }

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

    }

}