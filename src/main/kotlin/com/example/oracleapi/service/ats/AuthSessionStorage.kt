package com.example.oracleapi.service.ats

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Component
class AuthSessionStorage {
    private val log = LoggerFactory.getLogger(AuthSessionStorage::class.java)

    private val sessions = ConcurrentHashMap<String, AuthSession>()
    private val phoneIndex = ConcurrentHashMap<String, String>()
    private val dialedNumberIndex = ConcurrentHashMap<String, String>()

    fun createSession(
        actionId: String,
        phoneNumber: String,
        userLogin: String? = null,
        dialedNumber: String? = null,
        authCode: String? = null
    ): AuthSession {
        val session = AuthSession(
            actionId = actionId,
            phoneNumber = phoneNumber,
            userLogin = userLogin,
            dialedNumber = dialedNumber,
            status = "INITIATED",
            createdAt = LocalDateTime.now(),
            expiresAt = null,
            answeredAt = null,
            duration = null,
            channel = null,
            destChannel = null,
            uniqueId = null
        )

        sessions[actionId] = session
        phoneIndex[phoneNumber] = actionId

        dialedNumber?.let {
            dialedNumberIndex[it] = actionId
            log.debug("📇 Сессия $actionId проиндексирована по dialedNumber: $it")
        }

        indexPhoneVariants(phoneNumber, actionId)
        dialedNumber?.let { indexPhoneVariants(it, actionId) }

        log.info("🔐 Создана сессия: actionId={}, phone={}, dialed={}",
            actionId, phoneNumber, dialedNumber)

        return session
    }

    private fun indexPhoneVariants(number: String, actionId: String) {
        val withoutPlus = number.removePrefix("+")
        if (withoutPlus != number) {
            phoneIndex[withoutPlus] = actionId
        }

        val last10 = number.takeLast(10)
        if (last10.length == 10 && last10 != number && last10 != withoutPlus) {
            phoneIndex[last10] = actionId
        }

        val last11 = number.takeLast(11)
        if (last11.length == 11 && last11 != number && last11 != withoutPlus && last11 != last10) {
            phoneIndex[last11] = actionId
        }
    }

    fun getSessionByPhone(phone: String): AuthSession? {
        log.debug("🔍 Поиск сессии по номеру: phone={}", phone)

        var actionId = phoneIndex[phone]
        if (actionId != null) {
            log.debug("✅ Найдена сессия по прямому совпадению: {} -> {}", phone, actionId)
            return sessions[actionId]
        }

        actionId = dialedNumberIndex[phone]
        if (actionId != null) {
            log.debug("✅ Найдена сессия по dialedNumber: {} -> {}", phone, actionId)
            return sessions[actionId]
        }

        val phoneVariants = generatePhoneVariants(phone)
        for (variant in phoneVariants) {
            actionId = phoneIndex[variant]
            if (actionId != null) {
                log.debug("✅ Найдена сессия по частичному совпадению: {} -> {}", variant, actionId)
                return sessions[actionId]
            }

            actionId = dialedNumberIndex[variant]
            if (actionId != null) {
                log.debug("✅ Найдена сессия по dialedNumber (частичное): {} -> {}", variant, actionId)
                return sessions[actionId]
            }
        }

        log.debug("❌ Сессия не найдена для номера: {}", phone)
        return null
    }

    private fun generatePhoneVariants(phone: String): List<String> {
        val variants = mutableListOf<String>()
        variants.add(phone)

        if (phone.startsWith("+")) {
            variants.add(phone.substring(1))
        }

        if (!phone.startsWith("+")) {
            variants.add("+$phone")
        }

        val last10 = phone.takeLast(10)
        if (last10.length == 10) {
            variants.add(last10)
        }

        val last11 = phone.takeLast(11)
        if (last11.length == 11) {
            variants.add(last11)
        }

        if (phone.startsWith("8") && phone.length == 11) {
            variants.add("7" + phone.substring(1))
        }

        if (phone.startsWith("7") && phone.length == 11) {
            variants.add("8" + phone.substring(1))
        }

        if (phone.startsWith("800")) {
            val without800 = phone.substring(3)
            variants.add(without800)
            if (without800.startsWith("7")) {
                variants.add("8" + without800.substring(1))
            }
            if (without800.startsWith("8")) {
                variants.add("7" + without800.substring(1))
            }
        }

        return variants.distinct()
    }

    fun getSession(actionId: String): AuthSession? {
        return sessions[actionId]
    }

    fun updateSession(session: AuthSession) {
        sessions[session.actionId] = session
        session.dialedNumber?.let { dialedNumberIndex[it] = session.actionId }
    }

    fun removeSession(actionId: String) {
        val session = sessions.remove(actionId)
        if (session != null) {
            phoneIndex.remove(session.phoneNumber)
            session.dialedNumber?.let { dialedNumberIndex.remove(it) }
            log.debug("🗑️ Удалена сессия: {}", actionId)
        }
    }

    /**
     * Очистка просроченных сессий
     */
    fun cleanupExpiredSessions() {
        val now = LocalDateTime.now()
        val expiredSessions = sessions.values.filter { session ->
            session.expiresAt != null && session.expiresAt!!.isBefore(now)
        }

        if (expiredSessions.isNotEmpty()) {
            expiredSessions.forEach { session ->
                sessions.remove(session.actionId)
                phoneIndex.remove(session.phoneNumber)
                session.dialedNumber?.let { dialedNumberIndex.remove(it) }
                log.debug("🗑️ Удалена просроченная сессия: actionId={}, phone={}",
                    session.actionId, session.phoneNumber)
            }
            log.info("🧹 Очищено {} просроченных сессий", expiredSessions.size)
        }
    }

    fun getActiveSessions(): List<AuthSession> {
        return sessions.values.filter { it.status in listOf("INITIATED", "RINGING", "PROGRESS") }
    }

    fun clearAll() {
        sessions.clear()
        phoneIndex.clear()
        dialedNumberIndex.clear()
        log.warn("🧹 Все сессии очищены")
    }

    /**
     * Поиск сессии по уникальному ID канала
     */
    fun getSessionByUniqueId(uniqueId: String?): AuthSession? {
        if (uniqueId == null) return null

        return sessions.values.firstOrNull { session ->
            session.uniqueId == uniqueId
        }
    }

    /**
     * Поиск сессии по каналу
     */
    fun getSessionByChannel(channel: String?): AuthSession? {
        if (channel == null) return null

        return sessions.values.firstOrNull { session ->
            session.channel == channel || session.destChannel == channel
        }
    }

}

/**
 * Класс сессии авторизации
 */
data class AuthSession(
    val actionId: String,
    val phoneNumber: String,
    val userLogin: String? = null,
    val dialedNumber: String? = null,
    var status: String = "INITIATED",
    var createdAt: LocalDateTime,
    var expiresAt: LocalDateTime? = null,
    var answeredAt: LocalDateTime? = null,
    var duration: Long? = null,
    var channel: String? = null,
    var destChannel: String? = null,
    var uniqueId: String? = null,
    var authCode: String? = null
) {
    fun isWaiting(): Boolean {
        return status in listOf("INITIATED", "RINGING", "PROGRESS")
    }

    fun isAnswered(): Boolean {
        return status == "ANSWER"
    }

    fun isCompleted(): Boolean {
        return status in listOf("NOANSWER", "BUSY", "CONGESTION", "CANCELED", "FAILED")
    }

    fun isExpired(): Boolean {
        return expiresAt?.isBefore(LocalDateTime.now()) ?: false
    }
}