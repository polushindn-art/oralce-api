package com.example.oracleapi.service.ats

import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.maxUserAgn.MaxUserAgnRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.max.mainBoth.MainBotMessageService
import com.example.oracleapi.util.PhoneUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Сервис для обработки входящих вызовов АТС и отправки карточек абонентов сотрудникам в Telegram-бот.
 */
@Service
class CallNotificationService(
    private val mainBotMessageService: MainBotMessageService,
    private val maxUserAgnRepository: MaxUserAgnRepository,
    private val phonebookRepository: PhonebookRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val callCache = mutableMapOf<String, CallInfo>()

    /**
     * Обрабатывает входящий звонок со стандартным типом вызова (`internal`).
     *
     * @param callerNumber Номер звонящего абонента.
     * @param internalNumber Внутренний номер сотрудника.
     * @return `true`, если уведомление успешно отправлено.
     */
    fun processIncomingCall(callerNumber: String, internalNumber: String): Boolean =
        processIncomingCallWithType(callerNumber, internalNumber, "internal")

    /**
     * Обрабатывает входящий звонок с указанием типа и отправляет карточку вызова в чат сотрудника.
     *
     * @param callerNumber Номер звонящего абонента.
     * @param internalNumber Внутренний номер сотрудника.
     * @param callType Тип вызова (например, `"internal"` или `"mobile"`).
     * @return `true`, если уведомление успешно отправлено.
     */
    fun processIncomingCallWithType(callerNumber: String, internalNumber: String, callType: String = "internal"): Boolean {
        val employeeInfo = findCallerInfo(internalNumber) ?: return false
        val phoneSot = employeeInfo.phoneSot?.takeIf { it.isNotBlank() } ?: return false
        val phoneTail = PhoneUtils.getPhoneTail(phoneSot)

        val userAgn = maxUserAgnRepository.findEmployeeByPhoneTail(phoneTail) ?: run {
            log.debug("Сотрудник $internalNumber не зарегистрирован в боте")
            return false
        }

        // 🛑 Проверка настройки: включены ли у сотрудника уведомления о звонках
        if (userAgn.notifCalls == 0) {
            log.debug("🔕 [CallNotification] У сотрудника $internalNumber уведомления о звонках отключены")
            return false
        }

        val chatId = userAgn.chatId.takeIf { !it.isNullOrBlank() } ?: return false

        if (phonebookRepository.findByPhoneTail(phoneTail).isEmpty()) return false

        val callerInfo = findCallerInfo(callerNumber)
        val message = buildCallerCard(callerInfo, callerNumber, callType)

        val fullName = callerInfo?.let { listOfNotNull(it.fname, it.nname).joinToString(" ") } ?: "Неизвестный"
        callCache["${internalNumber}_$callerNumber"] = CallInfo(
            internalNumber = internalNumber,
            externalNumber = callerNumber,
            callerName = fullName,
            phoneInt = callerInfo?.phoneInt,
            phoneSot = callerInfo?.phoneSot
        )
        if (callCache.size > 100) callCache.clear()

        val buttons = mutableListOf<List<Map<String, Any>>>()
        if (employeeInfo.phoneSot?.isNotBlank() == true) {
            buttons.add(listOf(mapOf("type" to "callback", "text" to "📱 Попросить перезвонить мне на сотовый", "payload" to "call_mobile_${internalNumber}_$callerNumber")))
        }
        buttons.add(listOf(mapOf("type" to "callback", "text" to "◀️ В меню", "payload" to "back_to_menu")))

        val response = if (buttons.isEmpty()) {
            mainBotMessageService.sendMessage(chatId, message, "markdown")
        } else {
            mainBotMessageService.sendMessageWithInlineKeyboard(chatId, message, buttons, "markdown")
        }

        if (!response.success) {
            log.error("Не удалось отправить уведомление в чат $chatId: ${response.error}")
        }

        return response.success
    }

    /**
     * Ищет информацию о сотруднике в справочнике по внутреннему, сотовому или внешнему номеру.
     *
     * @param phoneNumber Строка с номером телефона для поиска.
     * @return Найденный объект [Phonebook] или `null`.
     */
    @Transactional(readOnly = true)
    fun findCallerInfo(phoneNumber: String): Phonebook? =
        phonebookRepository.findByPhoneInt(phoneNumber).firstOrNull()
            ?: phonebookRepository.findByPhoneSot(phoneNumber).firstOrNull()
            ?: phonebookRepository.findByPbe(phoneNumber).firstOrNull()

    private fun buildCallerCard(caller: Phonebook?, callerNumber: String, callType: String = "internal"): String {
        val typeEmoji = if (callType == "mobile") "📱 *Звонок на сотовый*" else "📞 *Входящий звонок*"
        if (caller == null) return "$typeEmoji\n👤 Неизвестный\n📱 $callerNumber"

        val fullName = listOfNotNull(caller.fname, caller.nname).joinToString(" ")
        val isBirthday = caller.rdate?.let { it.month == LocalDate.now().month && it.dayOfMonth == LocalDate.now().dayOfMonth } ?: false

        val lines = mutableListOf(typeEmoji, "", "👤 *$fullName*")
        caller.dolgnost?.let { lines.add("📋 $it") }
        caller.otdel?.let { lines.add("🏢 $it") }

        val phones = mutableListOf<String>()
        caller.phoneInt?.let { phones.add("вн.$it") }
        caller.phoneSot?.let { phones.add("сот.${formatPhoneNumber(it)}") }
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

/**
 * Информация о совершенном вызове для кэширования.
 */
data class CallInfo(
    val internalNumber: String,
    val externalNumber: String,
    val callerName: String? = null,
    val phoneInt: String? = null,
    val phoneSot: String? = null
)