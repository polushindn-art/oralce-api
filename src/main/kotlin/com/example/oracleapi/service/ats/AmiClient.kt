package com.example.oracleapi.service.ats

import com.example.oracleapi.config.AsteriskProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.BufferedWriter
import java.nio.charset.StandardCharsets

@Service
class AmiClient(
    private val properties: AsteriskProperties
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Создаёт подключение к AMI и авторизуется
     * @return Pair<Socket, BufferedReader> или null при ошибке
     */
    fun connect(): Pair<Socket, BufferedReader>? {
        return try {
            val socket = Socket(properties.host, properties.port)
            val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))

            // Логин
            writer.write("Action: Login\r\n")
            writer.write("Username: ${properties.username}\r\n")
            writer.write("Secret: ${properties.secret}\r\n")
            writer.write("\r\n")
            writer.flush()

            // Проверяем ответ
            var authSuccess = false
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 5000) {
                val line = reader.readLine() ?: break
                if (line.contains("Authentication accepted")) {
                    authSuccess = true
                    break
                }
                if (line.contains("Response: Error") || line.contains("Message: Authentication failed")) {
                    break
                }
            }

            if (!authSuccess) {
                log.error("❌ Ошибка авторизации AMI")
                socket.close()
                return null
            }

            log.info("✅ AMI авторизация успешна")
            Pair(socket, reader)

        } catch (e: Exception) {
            log.error("❌ Ошибка подключения к AMI", e)
            null
        }
    }

    /**
     * Отправляет команду в AMI
     */
    fun sendCommand(socket: Socket, vararg commands: String): Boolean {
        return try {
            val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
            commands.forEach { command ->
                writer.write(command)
                writer.write("\r\n")
            }
            writer.write("\r\n")
            writer.flush()
            true
        } catch (e: Exception) {
            log.error("❌ Ошибка отправки команды AMI", e)
            false
        }
    }

}