package com.example.oracleapi.service.ats

import com.example.oracleapi.repository.phonebook.PhonebookRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader

@Service
class AmiEventListenerService(
    private val amiClient: AmiClientService,
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
                    //"Event: DialEnd" -> handleDialEnd(reader)
                    //"Event: Bridge" -> handleBridge(reader)
                    //"Event: BridgeEnter" -> handleBridgeEnter(reader)
                    //"Event: Hangup" -> handleHangup(reader)
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

}