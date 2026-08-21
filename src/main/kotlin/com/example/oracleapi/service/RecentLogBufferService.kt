package com.example.oracleapi.service

import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue
import org.springframework.beans.factory.annotation.Value

@Service
class RecentLogBufferService(
    @param:Value("\${app.logging.max-buffer-size:1000}") private val maxLines: Int
) {
    private val logBuffer = ArrayDeque<String>(maxLines)
    private val buffer = ConcurrentLinkedQueue<String>()
    private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
        .withZone(ZoneId.systemDefault())

    fun addLog(level: String, loggerName: String, message: String) {
        val timestamp = dateFormatter.format(Instant.now())
        val shortLogger = loggerName.substringAfterLast('.')
        val formattedLine = "[$timestamp] [$level] [$shortLogger]: $message"

        buffer.offer(formattedLine)

        // Удерживаем размер ровно в пределах 100 строк
        while (buffer.size > maxLines) {
            buffer.poll()
        }
    }

    fun getRecentLogs(): List<String> {
        return buffer.toList()
    }
}