package com.example.oracleapi.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

data class ErrorRecord(val timestamp: Instant, val type: String, val path: String)

data class ErrorDetail(val timestamp: String, val type: String, val path: String)

data class ErrorStats(
    val lastMinute: Int,
    val lastHour: Int,
    val lastDay: Int,
    val byTypeLastHour: Map<String, Int>,
    val recentErrors: List<ErrorDetail> // Лента последних ошибок
)

@Component
class ErrorCounter {
    private val errorRecords = ConcurrentLinkedQueue<ErrorRecord>()

    fun recordError(errorType: String, path: String) {
        errorRecords.add(ErrorRecord(Instant.now(), errorType, path))
        cleanOldErrors()
    }

    fun getStats(): ErrorStats {
        cleanOldErrors()
        val now = Instant.now()
        val minThreshold = now.minusSeconds(60)
        val hourThreshold = now.minusSeconds(3600)

        val lastMinuteList = errorRecords.filter { it.timestamp.isAfter(minThreshold) }
        val lastHourList = errorRecords.filter { it.timestamp.isAfter(hourThreshold) }

        // Берем последние 5 ошибок, отсортированные от свежих к старым
        val recent = errorRecords.sortedByDescending { it.timestamp }.take(5).map {
            // Форматируем время в формат ЧЧ:мм:сс
            val timeStr = it.timestamp.atZone(java.time.ZoneId.systemDefault()).toLocalTime().toString().substring(0, 8)
            ErrorDetail(timestamp = timeStr, type = it.type, path = it.path)
        }

        return ErrorStats(
            lastMinute = lastMinuteList.size,
            lastHour = lastHourList.size,
            lastDay = errorRecords.size,
            byTypeLastHour = lastHourList.groupingBy { it.type }.eachCount(),
            recentErrors = recent
        )
    }

    @Scheduled(fixedRate = 600_000)
    private fun cleanOldErrors() {
        val oneDayAgo = Instant.now().minusSeconds(86400)
        while (true) {
            val oldest = errorRecords.peek() ?: break
            if (oldest.timestamp.isBefore(oneDayAgo)) {
                errorRecords.poll()
            } else {
                break
            }
        }
    }
}