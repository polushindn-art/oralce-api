package com.example.oracleapi

import kotlin.random.Random

class Helper {
    companion object {
        const val SCHEME = "Qreal"
        const val DD = "DD"

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

    }

}