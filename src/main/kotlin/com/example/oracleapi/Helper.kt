package com.example.oracleapi

class Helper {
    companion object {
        const val SCHEME = "Qreal"
        fun currentTimestamp(): String {
            return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
}