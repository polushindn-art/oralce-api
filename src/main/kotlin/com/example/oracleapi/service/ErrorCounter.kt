package com.example.oracleapi.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.LinkedHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Запись о единичном сбое в системе.
 */
data class ErrorRecord(val timestamp: Instant, val type: String, val path: String)

/**
 * Детализированная информация об ошибке для отображения на фронтенд-дашборде.
 */
data class ErrorDetail(val timestamp: String, val type: String, val path: String)

/**
 * Общая статистика сбоев за разные периоды, сгруппированная по типам,
 * лента последних ошибок и таймлайн для построения графиков.
 */
data class ErrorStats(
    val lastMinute: Int,
    val lastHour: Int,
    val lastDay: Int,
    val byTypeLastHour: Map<String, Int>,
    val recentErrors: List<ErrorDetail>, // Лента последних ошибок (до 10 штук)
    val timeline: Map<String, Int>       // Данные по минутам для графика (формат ЧЧ:мм -> количество)
)

/**
 * Компонент для потокобезопасного сбора, хранения (в оперативной памяти)
 * и аналитики сбоев приложения.
 */
@Component
class ErrorCounter {
    // Потокобезопасная очередь для быстрого накопления ошибок в памяти
    private val errorRecords = ConcurrentLinkedQueue<ErrorRecord>()

    /**
     * Зарегистрировать новый сбой в системе.
     * Вызывается при перехвате ошибок в приложении.
     */
    fun recordError(errorType: String, path: String) {
        errorRecords.add(ErrorRecord(Instant.now(), errorType, path))
        cleanOldErrors()
    }

    /**
     * Сформировать полную статистику и агрегированные данные для дашборда.
     */
    fun getStats(): ErrorStats {
        cleanOldErrors()
        val now = Instant.now()
        val minThreshold = now.minusSeconds(60)
        val hourThreshold = now.minusSeconds(3600)

        // Фильтруем записи по временным срезам (минута, час)
        val lastMinuteList = errorRecords.filter { it.timestamp.isAfter(minThreshold) }
        val lastHourList = errorRecords.filter { it.timestamp.isAfter(hourThreshold) }

        // Берем последние 10 ошибок, сортируем от самых свежих к старым и форматируем время (ЧЧ:мм:сс)
        val recent = errorRecords.sortedByDescending { it.timestamp }.take(10).map {
            val timeStr = it.timestamp.atZone(java.time.ZoneId.systemDefault()).toLocalTime().toString().substring(0, 8)
            ErrorDetail(timestamp = timeStr, type = it.type, path = it.path)
        }

        // Генерируем таймлайн по минутам (последние 10 минут) с нулевыми значениями по умолчанию
        val timelineMap = LinkedHashMap<String, Int>()
        for (i in 9 downTo 0) {
            val minuteLabel = now.minusSeconds((i * 60).toLong())
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalTime().toString().substring(0, 5) // Формат ЧЧ:мм
            timelineMap[minuteLabel] = 0
        }

        // Заполняем таймлайн реальным количеством сбоев, попавших в каждую минуту
        lastHourList.forEach { record ->
            val minuteLabel = record.timestamp
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalTime().toString().substring(0, 5)
            if (timelineMap.containsKey(minuteLabel)) {
                timelineMap[minuteLabel] = timelineMap[minuteLabel]!! + 1
            }
        }

        return ErrorStats(
            lastMinute = lastMinuteList.size,
            lastHour = lastHourList.size,
            lastDay = errorRecords.size,
            byTypeLastHour = lastHourList.groupingBy { it.type }.eachCount(),
            recentErrors = recent,
            timeline = timelineMap
        )
    }

    /**
     * Фоновое задание: автоматическая очистка устаревших ошибок (старше 24 часов)
     * каждые 10 минут (600_000 миллисекунд) для предотвращения переполнения памяти.
     */
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