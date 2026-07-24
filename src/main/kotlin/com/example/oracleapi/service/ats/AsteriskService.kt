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

    fun originateCall(internalNumber: String, externalNumber: String, callerName: String? = null): Boolean {
        val connection = amiClient.connect() ?: return false
        val (socket, reader) = connection

        return try {
            val callerId = if (!callerName.isNullOrBlank()) {
                "\"$callerName\" <$externalNumber>"
            } else {
                externalNumber
            }

            amiClient.sendCommand(
                socket,
                "Action: Originate",
                "Channel: PJSIP/$internalNumber",
                "Exten: $externalNumber",
                "Context: from-internal",
                "Priority: 1",
                "CallerID: $callerId",
                "Timeout: 30000"
            )

            Thread.sleep(500)

            // Читаем ответ
            while (reader.ready()) {
                val responseLine = reader.readLine()
                log.info("📞 AMI ответ: $responseLine")
            }

            log.info("✅ Звонок инициирован: $internalNumber → $externalNumber")
            true

        } catch (e: Exception) {
            log.error("❌ Ошибка при вызове через AMI", e)
            false
        } finally {
            socket.close()
        }
    }
}