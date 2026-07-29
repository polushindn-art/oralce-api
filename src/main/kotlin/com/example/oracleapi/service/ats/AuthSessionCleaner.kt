package com.example.oracleapi.service.ats

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AuthSessionCleaner(
    private val authSessionStorage: AuthSessionStorage
) {

    /**
     * Очистка просроченных сессий каждые 30 секунд
     */
    @Scheduled(fixedDelay = 30000)
    fun cleanupExpiredSessions() {
        authSessionStorage.cleanupExpiredSessions()
    }
}