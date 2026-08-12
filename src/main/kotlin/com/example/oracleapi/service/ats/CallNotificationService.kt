package com.example.oracleapi.service.ats

import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.maxUserAgn.MaxUserAgnRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.max.mainBoth.MainBotMessageService
import com.example.oracleapi.util.PhoneUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CallNotificationService(
    private val mainBotMessageService: MainBotMessageService,
    private val maxUserAgnRepository: MaxUserAgnRepository,
    private val phonebookRepository: PhonebookRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val callCache = mutableMapOf<String, CallInfo>()

    fun processIncomingCall(callerNumber: String, internalNumber: String): Boolean {
        return processIncomingCallWithType(callerNumber, internalNumber, "internal")
    }

    fun processIncomingCallWithType(callerNumber: String, internalNumber: String, callType: String = "internal"): Boolean {
        log.info("📞 [CallNotification] Начало обработки звонка: caller=$callerNumber, internal=$internalNumber, type=$callType")

        // ✅ 1. Ищем сотрудника в phonebook по внутреннему номеру
        log.info("🔍 [CallNotification] Шаг 1: Поиск сотрудника в phonebook по внутреннему номеру: $internalNumber")
        val employeeInfo = findCallerInfo(internalNumber)
        if (employeeInfo == null) {
            log.warn("❌ [CallNotification] Не найден сотрудник в phonebook с номером: $internalNumber")
            return false
        }
        log.info("✅ [CallNotification] Найден сотрудник: ${employeeInfo.fname} ${employeeInfo.nname}, phoneInt=${employeeInfo.phoneInt}, phoneSot=${employeeInfo.phoneSot}")

        // ✅ 2. Берём сотовый номер сотрудника
        val phoneSot = employeeInfo.phoneSot?.takeIf { it.isNotBlank() }
        if (phoneSot == null) {
            log.warn("❌ [CallNotification] У сотрудника $internalNumber не указан сотовый номер")
            return false
        }
        log.info("✅ [CallNotification] Шаг 2: Сотовый номер сотрудника: $phoneSot")

        // ✅ 3. Ищем пользователя в max_user_agn по сотовому номеру
        val phoneTail = PhoneUtils.getPhoneTail(phoneSot)
        log.info("🔍 [CallNotification] Шаг 3: Поиск в max_user_agn по phoneTail=$phoneTail")

        val userAgn = maxUserAgnRepository.findEmployeeByPhoneTail(phoneTail)
        if (userAgn == null) {
            log.warn("❌ [CallNotification] Сотрудник $internalNumber не зарегистрирован в боте (phoneTail=$phoneTail)")
            return false
        }
        log.info("✅ [CallNotification] Найден пользователь в боте: userId=${userAgn.userId}, chatId=${userAgn.chatId}, userName=${userAgn.userName}")

        val chatId = userAgn.chatId
        if (chatId.isNullOrBlank()) {
            log.warn("⚠️ [CallNotification] Невалидный chatId для сотрудника $internalNumber: chatId=$chatId")
            return false
        }
        log.info("✅ [CallNotification] Шаг 4: chatId для отправки: $chatId")

        // ✅ 4. Проверяем, что это сотрудник (есть в phonebook)
        val isEmployee = phonebookRepository.findByPhoneTail(phoneTail).isNotEmpty()
        if (!isEmployee) {
            log.info("ℹ️ [CallNotification] Пользователь $internalNumber не является сотрудником, уведомление не отправляется")
            return false
        }
        log.info("✅ [CallNotification] Шаг 5: Пользователь является сотрудником")

        // ✅ 5. Получаем информацию о звонящем
        log.info("🔍 [CallNotification] Шаг 6: Поиск информации о звонящем: $callerNumber")
        val callerInfo = findCallerInfo(callerNumber)
        log.info("✅ [CallNotification] Информация о звонящем: ${callerInfo?.fname} ${callerInfo?.nname}")

        val message = buildCallerCard(callerInfo, callerNumber, callType)
        log.info("✅ [CallNotification] Шаг 7: Сообщение для отправки:\n$message")

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

        // ✅ 6. Формируем кнопки
        log.info("🔍 [CallNotification] Шаг 8: Формирование кнопок для сотрудника $internalNumber")
        val buttons = mutableListOf<List<Map<String, Any>>>()

        val employeePhoneSot = employeeInfo.phoneSot?.takeIf { it.isNotBlank() }
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
            log.info("✅ [CallNotification] Добавлена кнопка перезвона на сотовый: $employeePhoneSot")
        } else {
            log.info("ℹ️ [CallNotification] У сотрудника нет сотового, кнопка не добавлена")
        }

        //Добавляем кнопку "В меню" всегда
                buttons.add(
                    listOf(
                        mapOf(
                            "type" to "callback",
                            "text" to "◀️ В меню",
                            "payload" to "back_to_menu"
                        )
                    )
                )

        // ✅ 7. Отправляем через нового бота
        log.info("📤 [CallNotification] Шаг 9: Отправка уведомления в chatId=$chatId, кнопок=${buttons.size}")

        val response = if (buttons.isEmpty()) {
            mainBotMessageService.sendMessage(chatId, message, "markdown")
        } else {
            mainBotMessageService.sendMessageWithInlineKeyboard(chatId, message, buttons, "markdown")
        }

        if (response.success) {
            log.info("✅ [CallNotification] Уведомление успешно отправлено: $callerNumber → ${userAgn.userName} ($internalNumber)")
        } else {
            log.error("❌ [CallNotification] Не удалось отправить уведомление: ${response.error}")
        }

        return response.success
    }

    /*fun processIncomingCallWithType(callerNumber: String, internalNumber: String, callType: String = "internal"): Boolean {
        // ✅ Ищем пользователя в max_user_agn по phone_tail
        val phoneTail = PhoneUtils.getPhoneTail(internalNumber)
        val userAgn = maxUserAgnRepository.findEmployeeByPhoneTail(phoneTail)

        if (userAgn == null) {
            log.warn("❌ Не найден пользователь с номером: $internalNumber")
            return false
        }

        val chatId = userAgn.chatId
        if (chatId.isNullOrBlank()) {
            log.warn("⚠️ Невалидный chatId для пользователя $internalNumber")
            return false
        }

        // ✅ Проверяем, является ли пользователь сотрудником (есть в phonebook)
        val isEmployee = phonebookRepository.findByPhoneTail(phoneTail).isNotEmpty()

        if (!isEmployee) {
            log.info("ℹ️ Пользователь $internalNumber не является сотрудником, уведомление не отправляется")
            return false
        }

        val callerInfo = findCallerInfo(callerNumber)
        val message = buildCallerCard(callerInfo, callerNumber, callType)

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

        val buttons = mutableListOf<List<Map<String, Any>>>()

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

        // ✅ Отправляем через нового бота
        val response = if (buttons.isEmpty()) {
            mainBotMessageService.sendMessage(chatId, message, "markdown")
        } else {
            mainBotMessageService.sendMessageWithKeyboard(chatId, message, buttons, "markdown")
        }

        if (response.success) {
            log.info("✅ Уведомление отправлено: $callerNumber → ${userAgn.userName} ($internalNumber)")
        } else {
            log.error("❌ Не удалось отправить уведомление: ${response.error}")
        }

        return response.success
    }*/

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