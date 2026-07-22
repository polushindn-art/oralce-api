package com.example.oracleapi.service.ats

import com.example.oracleapi.repository.max.MaxUserRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.max.MessageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CallNotificationService(
    private val messageService: MessageService,
    private val maxUserRepository: MaxUserRepository,
    private val phonebookRepository: PhonebookRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun processIncomingCall(callerNumber: String, internalNumber: String): Boolean {
        // Ищем сотрудника в БД по внутреннему номеру
        val user = maxUserRepository.findByInternalNumber(internalNumber)
            ?: return false.also {
                log.warn("❌ Не найден сотрудник с внутренним номером: $internalNumber")
            }

        val userId = user.userId

        // Ищем звонящего в PHONEBOOK по номеру
        val callerName = findCallerName(callerNumber)
        val callerDisplay = callerName ?: callerNumber

        val message = """
            📞 *Входящий звонок*

            Вам звонит: *$callerDisplay* ($callerNumber)
        """.trimIndent()

        val response = messageService.sendMessageByUserId(userId, message, "markdown")

        if (response.success) {
            log.info("✅ Уведомление отправлено: $callerDisplay → ${user.userName} ($internalNumber)")
        } else {
            log.error("❌ Не удалось отправить уведомление: ${response.error}")
        }

        return response.success
    }

    private fun findCallerName(phoneNumber: String): String? {
        // Ищем по номеру в PHONEBOOK
        val phonebook = phonebookRepository.findByPhoneInt(phoneNumber)
            ?: return null

        return listOfNotNull(
            phonebook.nname,
            phonebook.fname,
        ).joinToString(" ")
    }
}