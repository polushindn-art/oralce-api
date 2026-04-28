package com.example.oracleapi

import kotlin.random.Random

object RfidGenerator {
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