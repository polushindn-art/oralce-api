package com.example.oracleapi.exception

import com.example.oracleapi.service.ErrorCounter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Глобальный перехватчик исключений для обработки непредвиденных ошибок в контроллерах.
 * Автоматически регистрирует сбои в счетчике для мониторинга, логирует их
 * и возвращает клиенту стандартизированный ответ в формате JSON.
 */
@ControllerAdvice
class GlobalErrorCounterAdvice(private val errorCounter: ErrorCounter) {

    private val log = LoggerFactory.getLogger(GlobalErrorCounterAdvice::class.java)

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<Any> {
        val path = request.servletPath

        // Пропускаем системные 404 и эндпоинты actuator, чтобы не портить статистику
        if (ex is NoResourceFoundException || path.startsWith("/actuator")) {
            throw ex
        }

        val errorType = ex.javaClass.simpleName

        // Передаем ошибку в счетчик для дашборда
        errorCounter.recordError(errorType, path)

        log.error("❌ [Global Error] Тип: {}, Путь: {}, Сообщение: {}", errorType, path, ex.message)

        // Формируем стандартизированный JSON-ответ для клиента
        val errorBody = mapOf(
            "timestamp" to java.time.Instant.now().toString(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Internal Server Error",
            "message" to (ex.message ?: "Неизвестная ошибка")
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody)
    }
}