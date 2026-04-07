package com.example.oracleapi

class Helper {
    companion object {
        const val SCHEME = "Qreal"
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

    }

}