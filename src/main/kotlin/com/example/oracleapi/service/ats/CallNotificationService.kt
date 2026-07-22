package com.example.oracleapi.service.ats

import com.example.oracleapi.service.max.MessageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CallNotificationService(
    private val messageService: MessageService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    // Временное хранилище: внутренний номер → user_id
    // В будущем заменить на базу данных
    private val userNumberMap = mutableMapOf(
        "1001" to "375366076",   // ← сюда вставить реальные user_id
        "1012" to "28255919",
        "205" to "200879804"
    )

    fun processIncomingCall(callerNumber: String, internalNumber: String): Boolean {
        val userId = userNumberMap[internalNumber]
            ?: return false.also {
                log.warn("❌ Не найден user_id для внутреннего номера: $internalNumber")
            }

        val message = """
            📞 *Входящий звонок*

            Номер звонящего: *$callerNumber*
            Внутренний номер: *$internalNumber*
        """.trimIndent()

        val response = messageService.sendMessageByUserId(userId, message, "markdown")

        if (response.success) {
            log.info("✅ Уведомление отправлено на номер $internalNumber (user_id: $userId)")
        } else {
            log.error("❌ Не удалось отправить уведомление на номер $internalNumber: ${response.error}")
        }

        return response.success
    }
}