package com.example.oracleapi.config

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import com.example.oracleapi.service.ErrorCounter
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct

/**
 * Конфигурация для автоматического перехвата всех логов уровня ERROR
 * через Logback Appender и передачи их в счетчик ошибок (ErrorCounter) для мониторинга.
 */
@Configuration
class ErrorLogListenerConfig(
    private val errorCounter: ErrorCounter
) {

    @PostConstruct
    fun init() {
        // Получаем контекст Logback для программной регистрации аппендера
        val context = org.slf4j.LoggerFactory.getILoggerFactory() as? ch.qos.logback.classic.LoggerContext
        context?.let { ctx ->
            // Цепляемся к корневому логгеру, чтобы перехватывать ошибки по всему приложению
            val rootLogger = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)

            // Инициализируем и регистрируем наш кастомный перехватчик логов
            val appender = SpringErrorAppender(errorCounter)
            appender.context = ctx
            appender.start()
            rootLogger.addAppender(appender)
        }
    }

    /**
     * Кастомный Logback Appender, который слушает поток логов
     * и отправляет зафиксированные ERROR-события на дашборд.
     */
    class SpringErrorAppender(private val errorCounter: ErrorCounter) : AppenderBase<ILoggingEvent>() {
        override fun append(eventObject: ILoggingEvent) {
            if (eventObject.level == ch.qos.logback.classic.Level.ERROR) {
                try {
                    // Имя класса ошибки (например, LazyInitializationException)
                    val errorType = eventObject.throwableProxy?.className?.substringAfterLast('.')
                        ?: "LogException"

                    // Имя класса, откуда был вызван лог
                    val className = eventObject.loggerName.substringAfterLast('.')

                    // Берем текст ошибки из исключения или из самого сообщения лога
                    val errorMessage = eventObject.throwableProxy?.message ?: eventObject.formattedMessage

                    // Делаем понятное и подробное описание для дашборда
                    // Например: "MaxMainPollingService: could not initialize proxy..."
                    val details = "$className: $errorMessage"

                    // Обрезаем, если текст слишком длинный, чтобы не ломать верстку таблицы
                    val truncatedDetails = if (details.length > 120) details.substring(0, 117) + "..." else details

                    // Фиксируем в счетчике
                    errorCounter.recordError(errorType, truncatedDetails)
                } catch (_: Exception) {
                    // Защита от рекурсии
                }
            }
        }
    }
}