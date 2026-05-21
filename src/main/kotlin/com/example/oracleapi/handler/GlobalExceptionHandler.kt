package com.example.oracleapi.handler

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.exception.DocumentNotFoundException
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import javax.naming.AuthenticationException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    // 1. Ошибка преобразования типа (pbe=abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        e: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        val paramName = e.name
        val requiredType = e.requiredType?.simpleName ?: "число"
        val wrongValue = e.value

        val message = when (requiredType) {
            "Long", "Integer", "Int" ->
                "Параметр '$paramName' должен быть целым числом. Получено: '$wrongValue'"
            "Boolean" ->
                "Параметр '$paramName' должен быть true или false. Получено: '$wrongValue'"
            else ->
                "Параметр '$paramName' имеет неверный тип. Ожидается: $requiredType"
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                MyApiResponse.unsuccess(
                    message = message,
                    path = request.requestURI
                )
            )
    }

    // 2. Отсутствует обязательный параметр
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(
        e: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                MyApiResponse.unsuccess(
                    message = "Отсутствует обязательный параметр: ${e.parameterName}",
                    path = request.requestURI
                )
            )
    }

    // 3. Некорректный JSON в теле запроса
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                MyApiResponse.unsuccess(
                    message = "Некорректный формат запроса. Проверьте тело запроса. \n$e",
                    path = request.requestURI
                )
            )
    }

    // 4. Неподдерживаемый HTTP метод
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        e: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(
                MyApiResponse.unsuccess(
                    message = "Метод ${e.method} не поддерживается для этого эндпоинта. Поддерживаемые методы: ${e.supportedMethods?.joinToString(", ")}",
                    path = request.requestURI
                )
            )
    }

    // 5. Ошибка аутентификации
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        e: AuthenticationException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                MyApiResponse.unsuccess(
                    message = "Требуется авторизация: ${e.message ?: "пожалуйста, войдите в систему"}",
                    path = request.requestURI
                )
            )
    }

    // 6. Ошибка доступа (недостаточно прав)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        e: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                MyApiResponse.unsuccess(
                    message = "Недостаточно прав для доступа к ресурсу: ${e.message ?: "доступ запрещен"}",
                    path = request.requestURI
                )
            )
    }

    // 7. Ресурс не найден (404) - для эндпоинтов API
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(
        e: NoHandlerFoundException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        logger.info("Эндпоинт не найден: {} {}", request.method, request.requestURI)
        return createNotFoundResponse(request)
    }

    // 8. Статический ресурс не найден (404) - для файлов и статики
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(
        e: NoResourceFoundException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        logger.debug("Статический ресурс не найден: {}", request.requestURI)
        return createNotFoundResponse(request)
    }

    // 9. Entity не найдена в базе данных
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(
        e: EntityNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                MyApiResponse.unsuccess(
                    message = e.message ?: "Запись не найдена",
                    path = request.requestURI
                )
            )
    }

    // 10. Ошибка при работе с базой данных
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(
        e: DataAccessException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        logger.error("Ошибка базы данных: ${e.message}", e)

        val message = if (isDevelopment()) {
            "Ошибка базы данных: ${e.message}"
        } else {
            "Ошибка при обращении к базе данных"
        }

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                MyApiResponse.unsuccess(
                    message = message,
                    path = request.requestURI
                )
            )
    }

    // 11. Пустой результат (например, при удалении несуществующей записи)
    @ExceptionHandler(EmptyResultDataAccessException::class)
    fun handleEmptyResult(
        e: EmptyResultDataAccessException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                MyApiResponse.unsuccess(
                    message = "Запись не найдена для удаления/обновления",
                    path = request.requestURI
                )
            )
    }

    // 12. Ошибка бизнес-логики
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        e: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                MyApiResponse.unsuccess(
                    message = e.message ?: "Неверный параметр запроса",
                    path = request.requestURI
                )
            )
    }

    // 13. Некорректный токен
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(
        e: InvalidTokenException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                MyApiResponse.unsuccess(
                    message = e.message ?: "Неверный или истекший токен",
                    path = request.requestURI
                )
            )
    }

    // 14. Обработчик RuntimeException
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(
        e: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        logger.error("RuntimeException: ${e.message}", e)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                MyApiResponse.unsuccess(
                    message = e.message ?: "Внутренняя ошибка сервера",
                    path = request.requestURI
                )
            )
    }

    // 15. Все остальные ошибки (500)
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        logger.error("Необработанное исключение: ${e.message}", e)

        val message = if (isDevelopment()) {
            "Внутренняя ошибка сервера: ${e.message}"
        } else {
            "Внутренняя ошибка сервера. Пожалуйста, попробуйте позже."
        }

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                MyApiResponse.unsuccess(
                    message = message,
                    path = request.requestURI
                )
            )
    }

    @ExceptionHandler(DocumentNotFoundException::class)
    fun handleDocumentNotFound(
        e: DocumentNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Nothing>> {
        logger.info("Документ не найден: ${e.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                MyApiResponse.unsuccess(
                    message = e.message ?: "Документ не найден",
                    path = request.requestURI
                )
            )
    }

    // Вспомогательный метод для создания ответа 404
    private fun createNotFoundResponse(request: HttpServletRequest): ResponseEntity<MyApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                MyApiResponse.unsuccess(
                    message = "Ресурс не найден: ${request.requestURI}",
                    path = request.requestURI
                )
            )
    }

    // Вспомогательный метод для определения окружения
    private fun isDevelopment(): Boolean {
        return System.getProperty("spring.profiles.active") == "dev" ||
                System.getenv("SPRING_PROFILES_ACTIVE") == "dev"
    }

    // Кастомное исключение для токенов
    class InvalidTokenException(message: String?) : RuntimeException(message)
}