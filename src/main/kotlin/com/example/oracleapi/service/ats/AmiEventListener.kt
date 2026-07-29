package com.example.oracleapi.service.ats

import com.example.oracleapi.repository.phonebook.PhonebookRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.time.LocalDateTime

@Service
class AmiEventListener(
    private val amiClient: AmiClient,
    private val callNotificationService: CallNotificationService,
    private val phonebookRepository: PhonebookRepository,
    private val authSessionStorage: AuthSessionStorage
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
        val connection = amiClient.connect() ?: return
        val (socket, reader) = connection

        try {
            log.info("✅ AMI слушатель запущен")
            amiClient.sendCommand(socket, "Action: Events", "EventMask: on")

            while (true) {
                val currentLine = reader.readLine() ?: break

                when (currentLine) {
                    "Event: DialBegin" -> handleDialBegin(reader)
                    "Event: DialEnd" -> handleDialEnd(reader)
                    "Event: Bridge" -> handleBridge(reader)
                    "Event: BridgeEnter" -> handleBridgeEnter(reader)
                    "Event: Hangup" -> handleHangup(reader)
                }
            }
        } catch (e: Exception) {
            log.error("❌ Ошибка в AMI слушателе", e)
        } finally {
            socket.close()
        }
    }

    private fun handleDialBegin(reader: BufferedReader) {
        var actionId: String? = null
        var callerNumber: String? = null
        var destCallerIdNum: String? = null
        var dialString: String? = null
        var uniqueId: String? = null
        var channel: String? = null
        var destChannel: String? = null

        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) break

            when {
                line.startsWith("ActionID: ") ->
                    actionId = line.substringAfter("ActionID: ").trim()

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

                line.startsWith("DestChannel: ") ->
                    destChannel = line.substringAfter("DestChannel: ").trim()
            }
        }

        val destination = destCallerIdNum ?: dialString
        val caller = callerNumber

        // ===== АВТОРИЗАЦИЯ =====
        var session: AuthSession? = null

        if (actionId != null) {
            session = authSessionStorage.getSession(actionId)
        }

        if (session == null && destination != null) {
            session = authSessionStorage.getSessionByPhone(destination)
        }

        if (session != null) {
            session.channel = channel
            session.destChannel = destChannel
            session.uniqueId = uniqueId
            session.status = "RINGING"
            authSessionStorage.updateSession(session)
            log.info(
                "🔐 Звонок для авторизации: actionId={}, phone={}, status=RINGING",
                actionId ?: "unknown", session.phoneNumber
            )
            return
        }

        // ===== ПРОВЕРКА 2: Это звонок с нашего городского номера? =====
        val isOurCityNumber = channel?.contains("DIANET_388585") == true

        if (!isOurCityNumber) {
            log.debug(
                "🚫 Пропускаем чужой звонок: channel={}, caller={}, dest={}",
                channel, caller, destination
            )
            return
        }

        // ===== ОБРАБОТКА ТОЛЬКО НАШИХ ЗВОНКОВ =====
        if (caller != null && destination != null && uniqueId != null) {
            if (processedUniqueIds.add(uniqueId)) {
                val internalCaller = channel?.let { ch ->
                    val match = Regex("PJSIP/(\\d+)").find(ch)
                    match?.groupValues?.get(1)
                } ?: caller

                val isExternal = destination.startsWith("8") || destination.startsWith("+7")

                if (isExternal) {
                    val ownerInternalNumber = findOwnerByPhoneNumber(destination)
                    if (ownerInternalNumber != null) {
                        log.info("📱 Звонок на сотовый: $internalCaller → $destination, владелец: $ownerInternalNumber")
                        callNotificationService.processIncomingCallWithType(
                            callerNumber = internalCaller,
                            internalNumber = ownerInternalNumber,
                            callType = "mobile"
                        )
                    } else {
                        log.info("📞 Внешний звонок (владелец не найден): $internalCaller → $destination")
                    }
                } else {
                    log.info("📞 Внутренний звонок: $caller → $destination")
                    callNotificationService.processIncomingCall(caller, destination)
                }
            }
        }
    }

    private fun handleDialEnd(reader: BufferedReader) {
        var actionId: String? = null
        var callerNumber: String? = null
        var destination: String? = null
        var dialStatus: String? = null
        var uniqueId: String? = null
        var duration: Long? = null
        var channel: String? = null

        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) break

            when {
                line.startsWith("ActionID: ") ->
                    actionId = line.substringAfter("ActionID: ").trim()

                line.startsWith("CallerIDNum: ") ->
                    callerNumber = line.substringAfter("CallerIDNum: ").trim()

                line.startsWith("DestCallerIDNum: ") ->
                    destination = line.substringAfter("DestCallerIDNum: ").trim()

                line.startsWith("DialStatus: ") ->
                    dialStatus = line.substringAfter("DialStatus: ").trim()

                line.startsWith("Uniqueid: ") ->
                    uniqueId = line.substringAfter("Uniqueid: ").trim()

                line.startsWith("Duration: ") ->
                    duration = line.substringAfter("Duration: ").trim().toLongOrNull()

                line.startsWith("Channel: ") ->
                    channel = line.substringAfter("Channel: ").trim()
            }
        }

        log.debug(
            "📋 DialEnd: actionId={}, destination={}, dialStatus={}, duration={}, channel={}",
            actionId, destination, dialStatus, duration, channel
        )

        // ===== ПРОВЕРКА 1: Это звонок для авторизации? =====
        var session: AuthSession? = null
        if (actionId != null) session = authSessionStorage.getSession(actionId)
        if (session == null && destination != null) session = authSessionStorage.getSessionByPhone(destination)

        if (session != null) {
            session.duration = duration

            when (dialStatus) {
                "ANSWER" -> {
                    // ❌ НЕ СТАВИМ answeredAt ЗДЕСЬ!
                    session.status = "ANSWER"
                }

                "CANCEL" -> {
                    session.status = "CANCELED"
                }

                "NOANSWER" -> {
                    session.status = "NOANSWER"
                }

                "BUSY" -> {
                    session.status = "BUSY"
                }

                else -> {
                    session.status = dialStatus ?: "UNKNOWN"
                }
            }

            session.expiresAt = LocalDateTime.now().plusSeconds(30)
            authSessionStorage.updateSession(session)
            return
        }

        // ===== ПРОВЕРКА 2: Это звонок с нашего городского номера (НЕ авторизация)? =====
        val isOurCityNumber = channel?.contains("DIANET_388585") == true

        if (!isOurCityNumber) {
            log.debug("🚫 Пропускаем чужой результат звонка: channel={}", channel)
            return
        }

        // ===== ОБРАБОТКА ТОЛЬКО НАШИХ ЗВОНКОВ (НЕ авторизация) =====
        // Сюда попадают только звонки, которые НЕ являются авторизацией
        if (dialStatus != null && dialStatus != "CANCEL") {
            when (dialStatus) {
                "ANSWER" -> log.info("✅ Абонент взял трубку $callerNumber")
                "NOANSWER" -> log.info("⏳ Абонент не ответил $callerNumber")
                "BUSY" -> log.info("📞 Линия занята $callerNumber")
                else -> log.info("ℹ️ Статус: $dialStatus")
            }
        }
    }

    private fun handleHangup(reader: BufferedReader) {
        var channel: String? = null
        var uniqueId: String? = null
        var cause: String? = null
        var causeTxt: String? = null
        var callerIdNum: String? = null
        var duration: String? = null

        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) break

            when {
                line.startsWith("Channel: ") -> channel = line.substringAfter("Channel: ").trim()
                line.startsWith("Uniqueid: ") -> uniqueId = line.substringAfter("Uniqueid: ").trim()
                line.startsWith("Cause: ") -> cause = line.substringAfter("Cause: ").trim()
                line.startsWith("Cause-txt: ") -> causeTxt = line.substringAfter("Cause-txt: ").trim()
                line.startsWith("CallerIDNum: ") -> callerIdNum = line.substringAfter("CallerIDNum: ").trim()
                line.startsWith("Duration: ") -> duration = line.substringAfter("Duration: ").trim()
            }
        }

        log.info(
            "📞 Hangup: channel={}, cause={}, causeTxt={}, caller={}, duration={}",
            channel, cause, causeTxt, callerIdNum, duration
        )

        var session = authSessionStorage.getSessionByChannel(channel)
        if (session == null && uniqueId != null) {
            session = authSessionStorage.getSessionByUniqueId(uniqueId)
        }

        if (session != null) {
            session.duration = duration?.toLongOrNull() ?: session.duration

            // ✅ Если статус уже финальный - НЕ МЕНЯЕМ!
            if (session.status == "HANGUP" || session.status == "HANGUP_NO_ANSWER") {
                session.expiresAt = LocalDateTime.now().plusSeconds(60)
                authSessionStorage.updateSession(session)
                return
            }

            // ✅ Если был BRIDGED - звонок ДОШЕЛ!
            if (session.status == "BRIDGED") {
                session.status = "HANGUP"  // Успех!
                session.expiresAt = LocalDateTime.now().plusSeconds(60)
                authSessionStorage.updateSession(session)
                return
            }

            // ❌ Если не было BRIDGED - звонок НЕ ДОШЕЛ
            session.status = when (cause) {
                "0", "16" -> "HANGUP_NO_ANSWER"
                "17" -> "BUSY"
                "18", "19" -> "NOANSWER"
                "21" -> "REJECTED"
                else -> "HANGUP_${cause}"
            }
            session.expiresAt = LocalDateTime.now().plusSeconds(60)
            authSessionStorage.updateSession(session)
            return
        }
    }

    private fun handleBridge(reader: BufferedReader) {
        var bridgeUniqueId: String? = null
        var bridgeType: String? = null
        var bridgeState: String? = null
        var channel1: String? = null
        var channel2: String? = null
        var uniqueId1: String? = null
        var uniqueId2: String? = null

        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) break
            when {
                line.startsWith("BridgeUniqueid: ") -> bridgeUniqueId = line.substringAfter("BridgeUniqueid: ").trim()
                line.startsWith("BridgeType: ") -> bridgeType = line.substringAfter("BridgeType: ").trim()
                line.startsWith("BridgeState: ") -> bridgeState = line.substringAfter("BridgeState: ").trim()
                line.startsWith("Channel1: ") -> channel1 = line.substringAfter("Channel1: ").trim()
                line.startsWith("Channel2: ") -> channel2 = line.substringAfter("Channel2: ").trim()
                line.startsWith("Uniqueid1: ") -> uniqueId1 = line.substringAfter("Uniqueid1: ").trim()
                line.startsWith("Uniqueid2: ") -> uniqueId2 = line.substringAfter("Uniqueid2: ").trim()
            }
        }
        // Нас интересует только создание моста (BridgeState: Up)
        if (bridgeState != "Up") return

        // Ищем сессию по каналам
        var session = authSessionStorage.getSessionByChannel(channel1)
        if (session == null) session = authSessionStorage.getSessionByChannel(channel2)
        if (session == null) session = authSessionStorage.getSessionByUniqueId(uniqueId1)
        if (session == null) session = authSessionStorage.getSessionByUniqueId(uniqueId2)

        if (session != null) {
            // ✅ ТОЛЬКО ЗДЕСЬ СТАВИМ answeredAt!
            session.answeredAt = LocalDateTime.now()
            session.status = "BRIDGED"
            authSessionStorage.updateSession(session)
        }
    }

    private fun findOwnerByPhoneNumber(phoneNumber: String): String? {
        val digits = phoneNumber.replace(Regex("[^0-9]"), "")
        val last10 = if (digits.length >= 10) digits.takeLast(10) else digits

        val variants = listOf(
            "8$last10",
            "+7$last10",
            last10,
            phoneNumber
        ).distinct()

        variants.forEach { variant ->
            phonebookRepository.findByPhoneSot(variant).firstOrNull()?.let {
                return it.phoneInt
            }
            phonebookRepository.findByPhoneInt(variant).firstOrNull()?.let {
                return it.phoneInt
            }
        }

        return null
    }

    private fun handleBridgeEnter(reader: BufferedReader) {
        val allLines = mutableListOf<String>()
        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) break
            allLines.add(line)
        }

        var channel: String? = null
        var uniqueId: String? = null

        for (line in allLines) {
            when {
                line.startsWith("Channel: ") -> channel = line.substringAfter("Channel: ").trim()
                line.startsWith("Uniqueid: ") -> uniqueId = line.substringAfter("Uniqueid: ").trim()
            }
        }

        // Ищем сессию по каналу или uniqueId
        var session = authSessionStorage.getSessionByChannel(channel)
        if (session == null && uniqueId != null) {
            session = authSessionStorage.getSessionByUniqueId(uniqueId)
        }

        if (session != null) {
            // ✅ ЗВОНОК ДОШЕЛ до абонента!
            session.answeredAt = LocalDateTime.now()
            session.status = "BRIDGED"
            authSessionStorage.updateSession(session)
        }
    }
}