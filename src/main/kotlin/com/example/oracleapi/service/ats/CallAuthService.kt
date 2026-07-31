package com.example.oracleapi.service.ats

import org.springframework.stereotype.Service
import java.util.*

@Service
class CallAuthService(
    private val authSessionStorage: AuthSessionStorage,
    private val asteriskService: AsteriskService
) {

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