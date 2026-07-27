package com.example.oracleapi.service.ats

import com.example.oracleapi.repository.phonebook.PhonebookRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader

@Service
class AmiEventListener(
    private val amiClient: AmiClient,
    private val callNotificationService: CallNotificationService,
    private val phonebookRepository: PhonebookRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val processedUniqueIds = mutableSetOf<String>()

    @PostConstruct
    fun startListening() {
        Thread { listenLoop() }.start()
    }

    private fun listenLoop() {
        while (true) {
            try {
                listenToAmi()
            } catch (e: Exception) {
                log.error("❌ Ошибка в AMI слушателе, переподключение через 10 секунд...", e)
                Thread.sleep(10000)
            }
        }
    }

    private fun listenToAmi() {
        val connection = amiClient.connect() ?: run {
            log.warn("⏳ Не удалось подключиться к AMI, ждём 5 секунд...")
            Thread.sleep(5000)
            return
        }

        val (socket, reader) = connection

        try {
            log.info("✅ AMI слушатель запущен")

            amiClient.sendCommand(socket, "Action: Events", "EventMask: on")

            while (true) {
                val currentLine = reader.readLine() ?: break
                if (currentLine == "Event: DialBegin") {
                    handleDialBegin(reader)
                }
            }

        } catch (e: Exception) {
            log.error("❌ Ошибка в AMI слушателе, переподключение через 10 секунд...", e)
            Thread.sleep(10000)
        } finally {
            try {
                socket.close()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun handleDialBegin(reader: BufferedReader) {
        var callerNumber: String? = null
        var destCallerIdNum: String? = null
        var dialString: String? = null
        var uniqueId: String? = null
        var channel: String? = null

        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) break

            when {
                line.startsWith("CallerIDNum: ") ->
                    callerNumber = line.substringAfter("CallerIDNum: ").trim()

                line.startsWith("DestCallerIDNum: ") ->
                    destCallerIdNum = line.substringAfter("DestCallerIDNum: ").trim()

                line.startsWith("DialString: ") ->
                    dialString = line.substringAfter("DialString: ").trim()

                line.startsWith("Uniqueid: ") ->
                    uniqueId = line.substringAfter("Uniqueid: ").trim()

                line.startsWith("Channel: ") ->
                    channel = line.substringAfter("Channel: ").trim()
            }
        }

        val destination = destCallerIdNum ?: dialString
        val caller = callerNumber

        if (caller != null && destination != null && uniqueId != null) {
            if (processedUniqueIds.add(uniqueId)) {
                // Извлекаем внутренний номер звонящего
                val internalCaller = channel?.let { ch ->
                    val match = Regex("PJSIP/(\\d+)").find(ch)
                    match?.groupValues?.get(1)
                } ?: caller

                // Проверяем, является ли destination внешним номером
                val isExternal = destination.startsWith("8") || destination.startsWith("+7")

                if (isExternal) {
                    // Ищем владельца внешнего номера
                    val ownerInternalNumber = findOwnerByPhoneNumber(destination)

                    if (ownerInternalNumber != null) {
                        // ✅ Это звонок на сотовый номер сотрудника!
                        log.info("📱 Звонок на сотовый: $internalCaller → $destination, владелец: $ownerInternalNumber")

                        // Передаём метку "сотовый" через специальный параметр
                        callNotificationService.processIncomingCallWithType(
                            callerNumber = internalCaller,
                            internalNumber = ownerInternalNumber,
                            callType = "mobile"
                        )
                    } else {
                        log.info("📞 Внешний звонок (владелец не найден): $internalCaller → $destination")
                    }
                } else {
                    // Внутренний звонок
                    log.info("📞 Внутренний звонок: $caller → $destination")
                    callNotificationService.processIncomingCall(caller, destination)
                }
            }
        }
    }

    // ✅ МЕТОД ДЛЯ ПОИСКА ВЛАДЕЛЬЦА
    private fun findOwnerByPhoneNumber(phoneNumber: String): String? {
        val digits = phoneNumber.replace(Regex("[^0-9]"), "")
        val last10 = if (digits.length >= 10) digits.takeLast(10) else digits

        val variants = listOf(
            "8$last10",
            "+7$last10",
            last10,
            phoneNumber
        ).distinct()

        log.info("🔍 Поиск владельца по вариантам: $variants")

        variants.forEach { variant ->
            phonebookRepository.findByPhoneSot(variant).firstOrNull()?.let {
                log.info("✅ Нашли владельца по сотовому: ${it.fname} ${it.nname} (${it.phoneInt})")
                return it.phoneInt
            }
            phonebookRepository.findByPhoneInt(variant).firstOrNull()?.let {
                log.info("✅ Нашли владельца по внутреннему: ${it.fname} ${it.nname} (${it.phoneInt})")
                return it.phoneInt
            }
        }

        log.warn("❌ Владелец не найден для номера: $phoneNumber")
        return null
    }
}