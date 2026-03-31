
import com.example.oracleapi.dto.common.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import javax.naming.AuthenticationException

@RestControllerAdvice
class GlobalExceptionHandler {

    // Ошибка преобразования типа (pbe=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        e: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        val paramName = e.name
        val requiredType = e.requiredType?.simpleName ?: "число"
        val wrongValue = e.value

        val message = when (requiredType) {
            "Long" -> "Параметр '$paramName' должен быть целым числом. Получено: '$wrongValue'"
            "Int" -> "Параметр '$paramName' должен быть целым числом. Получено: '$wrongValue'"
            else -> "Параметр '$paramName' имеет неверный тип. Ожидается: $requiredType"
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    message = message,
                    path = request.requestURI
                )
            )
    }

    // Отсутствует обязательный параметр
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(
        e: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    message = "Отсутствует обязательный параметр: ${e.parameterName}",
                    path = request.requestURI
                )
            )
    }

    // Ошибка аутентификации
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        e: AuthenticationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiResponse.error(
                    message = "Требуется авторизация: ${e.message}",
                    path = request.requestURI
                )
            )
    }

    // Ошибка доступа
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        e: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ApiResponse.error(
                    message = "Недостаточно прав для доступа: ${e.message}",
                    path = request.requestURI
                )
            )
    }

    // Ресурс не найден
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleResourceNotFound(
        e: NoHandlerFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    message = "Запрашиваемый ресурс не найден: ${request.requestURI}",
                    path = request.requestURI
                )
            )
    }

    // Некорректный токен
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(
        e: InvalidTokenException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    message = e.message ?: "Неверный или истекший токен",
                    path = request.requestURI
                )
            )
    }

    // IllegalArgumentException (наша валидация в сервисе)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        e: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    message = e.message ?: "Неверный параметр запроса",
                    path = request.requestURI
                )
            )
    }

    // Все остальные ошибки
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.error(
                    message = "Внутренняя ошибка сервера: ${e.message}",
                    path = request.requestURI
                )
            )
    }

    // Внутренний класс исключения
    class InvalidTokenException(message: String?) : RuntimeException(message)
}