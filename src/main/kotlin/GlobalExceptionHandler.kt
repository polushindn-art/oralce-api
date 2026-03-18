
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.naming.AuthenticationException

@RestControllerAdvice
open class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf(
            "status" to "error",
            "code" to HttpStatus.UNAUTHORIZED.value(),
            "message" to "Требуется авторизация."
        ))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf(
            "status" to "error",
            "code" to HttpStatus.FORBIDDEN.value(),
            "message" to "Недостаточно прав для доступа к данному ресурсу."
        ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception, request: WebRequest): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf(
            "status" to "error",
            "code" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "message" to "${ex.message} Произошла внутренняя ошибка сервера."
        ))
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleResourceNotFound(ex: NoHandlerFoundException, request: WebRequest): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "status" to "error",
            "code" to HttpStatus.NOT_FOUND.value(),
            "message" to "Запрашиваемый ресурс не найден."
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(InvalidTokenException::class)
    protected fun handleInvalidToken(ex: InvalidTokenException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.badRequest().body(mapOf(
            "status" to HttpStatus.BAD_REQUEST.value(), // Число
            "message" to ex.message.toString() , // Строка
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // Форматированная строка
        ))
    }

    @ExceptionHandler(AccessDeniedException::class)
    protected fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf(
            "status" to HttpStatus.FORBIDDEN.value(),
            "message" to ex.message.toString(),
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ))
    }

    class InvalidTokenException(message: String?) : RuntimeException(message)

}
