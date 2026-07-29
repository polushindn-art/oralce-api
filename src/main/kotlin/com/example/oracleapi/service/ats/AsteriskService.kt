package com.example.oracleapi.service.ats

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import java.nio.charset.StandardCharsets

@Service
class AsteriskService(
    private val amiClient: AmiClient
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun originateCall(internalNumber: String, externalNumber: String, callerName: String? = null, actionId: String? = null): Boolean {
        val connection = amiClient.connect() ?: return false
        val (socket, reader) = connection

        return try {
            val callerId = if (!callerName.isNullOrBlank()) {
                "\"$callerName\" <$externalNumber>"
            } else {
                externalNumber
            }

            val commands = mutableListOf(
                "Action: Originate",
                "Channel: PJSIP/$internalNumber",
                "Exten: $externalNumber",
                "Context: from-internal",
                "Priority: 1",
                "CallerID: $callerId",
                "Timeout: 30000"
            )

            if (actionId != null) {
                commands.add("Variable: AUTH_ACTION_ID=$actionId")
            }

            // ✅ Правильно: передаем socket и список команд
            amiClient.sendCommand(socket, *commands.toTypedArray())

            log.info("✅ Команда Originate отправлена: $internalNumber → $externalNumber")
            true

        } catch (e: Exception) {
            log.error("❌ Ошибка при отправке команды Originate", e)
            false
        } finally {
            socket.close()
        }
    }

    fun originateCallFromCity(externalNumber: String, callerId: String? = null, actionId: String? = null): Boolean {
        val connection = amiClient.connect() ?: return false
        val (socket, reader) = connection

        return try {
            val channel = "PJSIP/DIANET_388585"
            val callerIdValue = callerId ?: "Городской номер"

            val commands = mutableListOf(
                "Action: Originate",
                "Channel: $channel",
                "Exten: $externalNumber",
                "Context: from-internal",
                "Priority: 1",
                "CallerID: $callerIdValue",
                "Timeout: 30000"
            )

            if (actionId != null) {
                commands.add("Variable: AUTH_ACTION_ID=$actionId")
            }

            amiClient.sendCommand(socket, *commands.toTypedArray())

            Thread.sleep(500)

            // ✅ Читаем ответ, но НЕ ЛОГИРУЕМ каждую строчку
            var success = false
            while (reader.ready()) {
                val responseLine = reader.readLine()
                if (responseLine.contains("Response: Success")) {
                    success = true
                }
                if (responseLine.contains("Response: Error")) {
                    log.error("❌ Ошибка AMI: $responseLine")
                    success = false
                }
            }

            if (success) {
                log.info("✅ Исходящий звонок с городского (DIANET_388585): → $externalNumber")
            } else {
                log.error("❌ Не удалось инициировать звонок с городского: → $externalNumber")
            }

            success

        } catch (e: Exception) {
            log.error("❌ Ошибка при исходящем звонке с городского", e)
            false
        } finally {
            socket.close()
        }
    }

    /**
     * Звонок с голосовым кодом
     */
    fun originateCallWithVoiceAuth(
        externalNumber: String,
        actionId: String,
        authCode: String
    ): Boolean {
        val connection = amiClient.connect() ?: return false
        val (socket, _) = connection

        val digits = authCode.map { "digits/$it" }.joinToString("&")
        // Формируем: пауза 2 сек → цифры → пауза 2 сек → цифры → пауза 2 сек → цифры
        val playback = "silence/2&$digits&silence/2&$digits&silence/2&$digits"


        return try {
            val commands = listOf(
                "Action: Originate",
                "Channel: PJSIP/$externalNumber@DIANET_388585",
                "Application: Playback",
                "Data: $playback",  // ← ВСТРОЕННЫЕ ЦИФРЫ!
                "CallerID: $externalNumber",
                "Timeout: 30000",
                "Variable: AUTH_ACTION_ID=$actionId"
            )

            amiClient.sendCommand(socket, *commands.toTypedArray())
            log.info("📞 Голосовой звонок: $externalNumber, код: $authCode")
            true

        } catch (e: Exception) {
            log.error("❌ Ошибка голосового звонка", e)
            false
        } finally {
            socket.close()
        }
    }

}