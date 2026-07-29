package com.example.oracleapi.service.ats

// ✅ НЕ НУЖНО импортировать AuthSession - он в том же пакете
import com.example.oracleapi.dto.asterisk.AuthStatusResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class CallAuthService(
    private val authSessionStorage: AuthSessionStorage,
    private val asteriskService: AsteriskService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun initiateAuthCall(
        phoneNumber: String,
        userLogin: String,
        callerId: String,
        prefix: String?
    ): String {
        val actionId = UUID.randomUUID().toString()

        // Для хранения в сессии - сохраняем как есть
        val storagePhone = phoneNumber

        // Для набора в Asterisk - добавляем префикс
        val dialNumber = "$prefix$phoneNumber"

        // 1. Сохраняем сессию с номером как есть и набираемым номером
        authSessionStorage.createSession(actionId, storagePhone, userLogin, dialNumber)

        // 2. Звоним на внешний номер клиента через транк
        try {
            asteriskService.originateCallFromCity(
                externalNumber = phoneNumber,
                callerId = callerId,
                actionId = actionId
            )

        } catch (e: Exception) {
            authSessionStorage.removeSession(actionId)
            log.error("❌ Ошибка инициации звонка: {}", e.message, e)
            throw e
        }

        return actionId
    }

    fun checkAuthStatus(actionId: String): AuthStatusResponse {
        val session = authSessionStorage.getSession(actionId) ?: return AuthStatusResponse(
            success = false,
            status = "NOT_FOUND",
            message = "Сессия не найдена или истекла"
        )

        if (session.isExpired()) {
            authSessionStorage.removeSession(actionId)
            return AuthStatusResponse(
                success = false,
                status = "TIMEOUT",
                message = "Время ожидания истекло"
            )
        }

        return when (session.status) {
            // ✅ УСПЕХ - звонок дошел
            "HANGUP" -> {
                AuthStatusResponse(
                    success = true,
                    status = "ANSWERED",
                    message = "✅ Авторизация успешна! Звонок дошел до абонента",
                    phone = session.phoneNumber,
                    userLogin = session.userLogin,
                    duration = session.duration ?: 0,
                    answeredAt = session.answeredAt ?: LocalDateTime.now()
                )
            }

            // ⏳ Ожидание
            "BRIDGED" -> AuthStatusResponse(
                success = false,
                status = "IN_PROGRESS",
                message = "⏳ Звонок в процессе...",
                phone = session.phoneNumber
            )

            "ANSWER" -> AuthStatusResponse(
                success = false,
                status = "WAITING",
                message = "⏳ Ожидание соединения...",
                phone = session.phoneNumber
            )

            // ❌ НЕУДАЧА - звонок не дошел
            "NOANSWER" -> AuthStatusResponse(
                success = false,
                status = "NOANSWER",
                message = "❌ Абонент не ответил",
                phone = session.phoneNumber
            )

            "BUSY" -> AuthStatusResponse(
                success = false,
                status = "BUSY",
                message = "📞 Линия занята",
                phone = session.phoneNumber
            )

            "CANCELED" -> AuthStatusResponse(
                success = false,
                status = "CANCELED",
                message = "❌ Звонок отменен",
                phone = session.phoneNumber
            )

            "HANGUP_NO_ANSWER" -> AuthStatusResponse(
                success = false,
                status = "HANGUP",
                message = "❌ Звонок не дошел",
                phone = session.phoneNumber
            )

            "INITIATED", "RINGING", "PROGRESS" -> AuthStatusResponse(
                success = false,
                status = session.status,
                message = when (session.status) {
                    "INITIATED" -> "⏳ Звонок инициируется..."
                    "RINGING" -> "📞 Идет вызов..."
                    "PROGRESS" -> "⏳ Устанавливается соединение..."
                    else -> "⏳ Ожидание..."
                },
                phone = session.phoneNumber
            )

            else -> AuthStatusResponse(
                success = false,
                status = session.status,
                message = "⏳ Ожидание...",
                phone = session.phoneNumber
            )
        }
    }

    fun getActiveSessions(): List<AuthSession> {
        return authSessionStorage.getActiveSessions()
    }

    /**
     * Инициация голосовой авторизации (звонок с кодом)
     */
    fun initiateVoiceAuth(phoneNumber: String): VoiceAuthResult {
        val actionId = UUID.randomUUID().toString()
        val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
        val dialNumber = cleanPhone  // ← 89635328259
        val authCode = generateAuthCode()

        authSessionStorage.createSession(
            actionId = actionId,
            phoneNumber = cleanPhone,
            dialedNumber = dialNumber,
            authCode = authCode
        )

        asteriskService.originateCallWithVoiceAuth(
            externalNumber = dialNumber,
            actionId = actionId,
            authCode = authCode
        )

        return VoiceAuthResult(
            phone = cleanPhone,
            authCode = authCode,
            actionId = actionId
        )
    }

    private fun generateAuthCode(): String {
        return (1000..9999).random().toString().padStart(4, '0')
    }

    data class VoiceAuthResult(
        val actionId: String,
        val authCode: String,
        val phone: String
    )

}