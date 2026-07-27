package com.example.oracleapi.service.ats

import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.max.MaxUserRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.max.MessageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CallNotificationService(
    private val messageService: MessageService,
    private val maxUserRepository: MaxUserRepository,
    private val phonebookRepository: PhonebookRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val callCache = mutableMapOf<String, CallInfo>()

    fun processIncomingCall(callerNumber: String, internalNumber: String): Boolean {
        return processIncomingCallWithType(callerNumber, internalNumber, "internal")
    }

    fun processIncomingCallWithType(callerNumber: String, internalNumber: String, callType: String = "internal"): Boolean {
        val user = maxUserRepository.findByInternalNumber(internalNumber)
            ?: return false.also {
                log.warn("❌ Не найден сотрудник с внутренним номером: $internalNumber")
            }

        val userId = user.userId
        if (userId.isBlank() || userId == "0") {
            log.warn("⚠️ Невалидный userId для сотрудника $internalNumber")
            return false
        }

        val callerInfo = findCallerInfo(callerNumber)
        val message = buildCallerCard(callerInfo, callerNumber, callType)

        // Кэшируем информацию
        val fullName = callerInfo?.let {
            listOfNotNull(it.fname, it.nname).joinToString(" ")
        } ?: "Неизвестный"
        val callKey = "${internalNumber}_$callerNumber"
        callCache[callKey] = CallInfo(
            internalNumber = internalNumber,
            externalNumber = callerNumber,
            callerName = fullName,
            phoneInt = callerInfo?.phoneInt,
            phoneSot = callerInfo?.phoneSot
        )
        cleanCache()

        // Формируем кнопки
        val buttons = mutableListOf<List<Map<String, Any>>>()

        // ✅ Для ВСЕХ звонков (и mobile, и internal) — кнопка, если у сотрудника есть сотовый
        val employeeInfo = findCallerInfo(internalNumber)
        val employeePhoneSot = employeeInfo?.phoneSot?.takeIf { it.isNotBlank() }

        if (employeePhoneSot != null) {
            buttons.add(
                listOf(
                    mapOf(
                        "type" to "callback",
                        "text" to "📱 Попросить перезвонить мне на сотовый",
                        "payload" to "call_mobile_${internalNumber}_$callerNumber"
                    )
                )
            )
        }

        // Отправка сообщения
        val response = if (buttons.isEmpty()) {
            log.info("📝 Нет сотового номера у сотрудника, отправляем сообщение без кнопок")
            messageService.sendMessageByUserId(userId, message, "markdown")
        } else {
            messageService.sendMessageWithKeyboard(userId, message, buttons, "markdown")
        }

        if (response.success) {
            log.info("✅ Уведомление отправлено: $callerNumber → ${user.userName} ($internalNumber)")
        } else {
            log.error("❌ Не удалось отправить уведомление: ${response.error}")
        }

        return response.success
    }

    private fun cleanCache() {
        if (callCache.size > 100) callCache.clear()
    }

    fun findCallerInfo(phoneNumber: String): Phonebook? {
        val byPhoneInt = phonebookRepository.findByPhoneInt(phoneNumber)
        if (byPhoneInt.isNotEmpty()) return byPhoneInt.first()

        val byPhoneSot = phonebookRepository.findByPhoneSot(phoneNumber)
        if (byPhoneSot.isNotEmpty()) return byPhoneSot.first()

        val byPbe = phonebookRepository.findByPbe(phoneNumber)
        if (byPbe.isNotEmpty()) return byPbe.first()

        return null
    }

    private fun buildCallerCard(caller: Phonebook?, callerNumber: String, callType: String = "internal"): String {
        val typeEmoji = if (callType == "mobile") "📱 *Звонок на сотовый*" else "📞 *Входящий звонок*"

        if (caller == null) {
            return """
            $typeEmoji
            👤 Неизвестный
            📱 $callerNumber
        """.trimIndent()
        }

        val fullName = listOfNotNull(caller.fname, caller.nname).joinToString(" ")
        val today = LocalDate.now()
        val isBirthday = caller.rdate?.let { birthDate ->
            birthDate.month == today.month && birthDate.dayOfMonth == today.dayOfMonth
        } ?: false

        val lines = mutableListOf<String>()
        lines.add(typeEmoji)
        lines.add("")
        lines.add("👤 *$fullName*")
        caller.dolgnost?.let { lines.add("📋 $it") }
        caller.otdel?.let { lines.add("🏢 $it") }

        val phones = mutableListOf<String>()
        caller.phoneInt?.let { phones.add("вн.$it") }
        caller.phoneSot?.let {
            val formatted = formatPhoneNumber(it)
            phones.add("сот.$formatted")
        }
        if (phones.isNotEmpty()) lines.add("📱 ${phones.joinToString(" | ")}")

        caller.email?.let { lines.add("📧 $it") }
        if (isBirthday) {
            lines.add("")
            lines.add("🎂 *СЕГОДНЯ ДР!* 🥳 Не забываем поздравить")
        }

        return lines.joinToString("\n")
    }

    fun formatPhoneNumber(phone: String): String {
        if (phone.isEmpty()) return phone
        return when {
            phone.startsWith("8") && phone.length == 11 -> "+7${phone.substring(1)}"
            phone.startsWith("8") && phone.length == 10 -> "+7$phone"
            else -> phone
        }
    }
}

data class CallInfo(
    val internalNumber: String,
    val externalNumber: String,
    val callerName: String? = null,
    val phoneInt: String? = null,
    val phoneSot: String? = null
)