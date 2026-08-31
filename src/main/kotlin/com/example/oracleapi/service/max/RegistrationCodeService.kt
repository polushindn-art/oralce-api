package com.example.oracleapi.service.max

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Component
class RegistrationCodeService {

    private val codeStorage = ConcurrentHashMap<String, CodeInfo>()

    /**
     * Сохранить код с номером телефона и типом бота
     */
    fun saveCode(code: String, phone: String, botType: String) {
        codeStorage[code] = CodeInfo(
            phone = phone,
            botType = botType,
            createdAt = System.currentTimeMillis(),
            attempts = 0
        )
        println("💾 Код сохранен: $code → $phone")
    }

    /**
     * Проверить код и получить информацию
     */
    fun verifyCode(code: String): CodeInfo? {
        val info = codeStorage[code] ?: return null

        // Проверяем срок действия (5 минут)
        if (System.currentTimeMillis() - info.createdAt > 300000) {
            codeStorage.remove(code)
            return null
        }

        // Проверяем попытки (максимум 3)
        if (info.attempts >= 3) {
            codeStorage.remove(code)
            return null
        }

        info.attempts++
        return info
    }

    /**
     * Удалить код после успешного использования
     */
    fun removeCode(code: String) {
        codeStorage.remove(code)
        println("🗑️ Код удален: $code")
    }

    /**
     * Проверить, существует ли код
     */
    fun hasCode(code: String): Boolean {
        return codeStorage.containsKey(code)
    }

    /**
     * Получить информацию о коде
     */
    fun getCodeInfo(code: String): CodeInfo? {
        return codeStorage[code]
    }

    /**
     * Очистить просроченные коды
     */
    fun cleanExpiredCodes() {
        val now = System.currentTimeMillis()
        codeStorage.entries.removeAll { entry ->
            now - entry.value.createdAt > 300000
        }
    }

    data class CodeInfo(
        val phone: String,
        val botType: String,
        val createdAt: Long,
        var attempts: Int = 0
    )
}