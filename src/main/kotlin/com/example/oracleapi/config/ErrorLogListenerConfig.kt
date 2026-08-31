package com.example.oracleapi.config

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import com.example.oracleapi.service.ErrorCounter
import com.example.oracleapi.service.RecentLogBuffer
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct

/**
 * Конфигурация для автоматического перехвата логов:
 * 1. Ошибок ERROR (для счетчика ошибок и дашборда).
 * 2. Всех логов (для буфера последних 100 строк).
 */
@Configuration
class ErrorLogListenerConfig(
    private val errorCounter: ErrorCounter,
    private val logBufferService: RecentLogBuffer
) {

    @PostConstruct
    fun init() {
        val context = org.slf4j.LoggerFactory.getILoggerFactory() as? ch.qos.logback.classic.LoggerContext
        context?.let { ctx ->
            val rootLogger = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)

            // 1. Ваш оригинальный аппендер для ошибок (оставляем без изменений)
            val errorAppender = SpringErrorAppender(errorCounter)
            errorAppender.context = ctx
            errorAppender.start()
            rootLogger.addAppender(errorAppender)

            // 2. Новый аппендер для сбора всех логов в буфер (последние 100 строк)
            val recentAppender = RecentLogAppender(logBufferService)
            recentAppender.context = ctx
            recentAppender.start()
            rootLogger.addAppender(recentAppender)
        }
    }

    /**
     * Ваш оригинальный аппендер для перехвата ERROR.
     */
    class SpringErrorAppender(private val errorCounter: ErrorCounter) : AppenderBase<ILoggingEvent>() {
        override fun append(eventObject: ILoggingEvent) {
            if (eventObject.level == ch.qos.logback.classic.Level.ERROR) {
                try {
                    val errorType = eventObject.throwableProxy?.className?.substringAfterLast('.')
                        ?: "LogException"
                    val className = eventObject.loggerName.substringAfterLast('.')
                    val errorMessage = eventObject.throwableProxy?.message ?: eventObject.formattedMessage
                    val details = "$className: $errorMessage"
                    val truncatedDetails = if (details.length > 120) details.substring(0, 117) + "..." else details
                    errorCounter.recordError(errorType, truncatedDetails)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Новый аппендер, который слушает ВСЕ логи и складывает их в буфер.
     */
    class RecentLogAppender(private val logBufferService: RecentLogBuffer) : AppenderBase<ILoggingEvent>() {
        override fun append(eventObject: ILoggingEvent) {
            try {
                logBufferService.addLog(
                    level = eventObject.level.toString(),
                    loggerName = eventObject.loggerName,
                    message = eventObject.formattedMessage
                )
            } catch (_: Exception) {}
        }
    }
}