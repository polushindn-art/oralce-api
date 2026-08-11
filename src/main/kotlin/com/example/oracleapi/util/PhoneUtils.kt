package com.example.oracleapi.util

class PhoneUtils {
    companion object {

        /**
         * Очищает номер телефона от всех нецифровых символов
         */
        fun cleanPhone(phone: String?): String {
            if (phone == null) return ""
            return phone.replace(Regex("[^0-9]"), "")
        }

        /**
         * Получает "хвост" номера (последние 10 цифр)
         * Для поиска по индексированному полю phone_tail
         */
        fun getPhoneTail(phone: String?): String {
            val digitsOnly = cleanPhone(phone)
            return if (digitsOnly.length >= 10) {
                digitsOnly.takeLast(10)
            } else {
                digitsOnly
            }
        }

        fun phone8(phone: String?): String {
            return "8${getPhoneTail(phone)}"
        }
    }
}