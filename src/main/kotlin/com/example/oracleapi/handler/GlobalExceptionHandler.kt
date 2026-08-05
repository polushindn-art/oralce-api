package com.example.oracleapi.handler

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.exception.DocumentNotFoundException
import com.example.oracleapi.exception.OracleException
import com.example.oracleapi.util.OracleErrorParser
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
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.*
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.sql.SQLException
import java.time.LocalDateTime
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
        val msg = e.message ?: ""

        // Пытаемся извлечь имя поля из сообщения Jackson
        val fieldName =  extractFieldName(msg) ?: extractMissingFieldName(msg)

        logger.debug(msg)

        val errorDetail = when {
            msg.contains("missing (therefore NULL) value for creator parameter") -> {
                val missingField = extractMissingFieldName(msg)
                if (missingField != null) {
                    "Поле '$missingField' обязательно для заполнения и не может быть пропущено"
                } else {
                    "Отсутствует обязательное поле. Проверьте тело запроса."
                }
            }

            msg.contains("Cannot deserialize value of type `java.time.LocalDate` from Null value") ->
                "Дата не может быть null. Пожалуйста, либо передайте корректную дату, либо уберите поле из запроса (если оно опционально)."

            msg.contains("Cannot deserialize value of type `java.time.LocalDateTime` from Null value") ->
                "Дата и время не могут быть null. Пожалуйста, либо передайте корректную дату/время, либо уберите поле из запроса (если оно опционально)."

            msg.contains("Cannot deserialize value of type `long` from Boolean value") ->
                if (fieldName != null) "Поле '$fieldName' должно быть числом, а не true/false"
                else "Неверный тип данных. Ожидается число, получено true/false."

            msg.contains("Cannot deserialize value of type `long` from String value") ->
                if (fieldName != null) "Поле '$fieldName' должно быть числом, а не строкой"
                else "Неверный тип данных. Ожидается число, получена строка."

            msg.contains("Cannot deserialize value of type `java.lang.Boolean`") ->
                if (fieldName != null) "Поле '$fieldName' должно быть true или false"
                else "Неверное булево значение. Ожидается true или false."

            msg.contains("Cannot deserialize value of type `java.time.LocalDate`") ->
                if (fieldName != null) "Поле '$fieldName' имеет неверный формат даты. Ожидается dd.MM.yyyy"
                else "Неверный формат даты. Ожидается dd.MM.yyyy"

            msg.contains("Cannot deserialize value of type `java.time.LocalDateTime`") ->
                if (fieldName != null) "Поле '$fieldName' имеет неверный формат даты и времени. Ожидается dd.MM.yyyy HH:mm:ss"
                else "Неверный формат даты и времени. Ожидается dd.MM.yyyy HH:mm:ss."

            msg.contains("Cannot deserialize value of type `java.lang.Long`") ->
                if (fieldName != null) "Поле '$fieldName' должно быть целым числом"
                else "Неверный формат числа. Ожидается целое число."

            msg.contains("Cannot deserialize value of type `long` from number") ->
                if (fieldName != null) "Поле '$fieldName' должно быть целым числом (без дробной части)"
                else "Неверный формат числа. Ожидается целое число."

            msg.contains("Cannot construct instance") ->
                "Неверная структура JSON. Проверьте корректность запроса (возможно, лишние кавычки или запятые)."

            else ->
                "Некорректный формат запроса. Проверьте тело запроса."
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                MyApiResponse.unsuccess(
                    errorDetail,
                    path = request.requestURI
                )
            )
    }

    private fun extractFieldName(msg: String): String? {
        // Формат: "Cannot deserialize value of type ... for field name 'pricecs'"
        val pattern1 = Regex("for field name '(.+?)'")
        pattern1.find(msg)?.let { return it.groupValues[1] }

        // Формат: "Cannot deserialize value of type ... from String value (token `JsonToken.VALUE_STRING`); nested exception is ..."
        val pattern2 = Regex("Cannot deserialize value of type `.*` from .+ value for field name '(.+?)'")
        pattern2.find(msg)?.let { return it.groupValues[1] }

        return null
    }

    private fun extractMissingFieldName(msg: String): String? {
        // Формат: "missing (therefore NULL) value for creator parameter pricecs"
        val pattern = Regex("creator parameter (.+?)(?: |\\)|$)")
        return pattern.find(msg)?.groupValues?.get(1)
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
                    message = "Метод ${e.method} не поддерживается для этого эндпоинта. Поддерживаемые методы: ${
                        e.supportedMethods?.joinToString(
                            ", "
                        )
                    }",
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

    @ExceptionHandler(SQLException::class)
    fun handleSqlException(ex: SQLException): ResponseEntity<MyApiResponse<Map<String, Any?>>> {
        val oracleEx = OracleErrorParser.parse(ex)

        val status = when {
            oracleEx.oracleCode in 20000..20999 -> HttpStatus.BAD_REQUEST   // бизнес-ошибки PL/SQL
            oracleEx.oracleCode in 1..19999 -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.BAD_REQUEST
        }

        return ResponseEntity
            .status(status)
            .body(
                MyApiResponse(
                    success = false,
                    message = oracleEx.message,
                    timestamp = LocalDateTime.now(),
                    data = mapOf(
                        "oracleCode" to oracleEx.oracleCode
                    ).plus(
                        if (oracleEx.details != null) mapOf("details" to oracleEx.details) else emptyMap()
                    )
                )
            )
    }

    @ExceptionHandler(OracleException::class)
    fun handleOracleException(
        ex: OracleException,
        request: HttpServletRequest
    ): ResponseEntity<MyApiResponse<Map<String, Any?>>> {

        val status = when {
            ex.oracleCode in 20000..20999 -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return ResponseEntity
            .status(status)
            .body(
                MyApiResponse(
                    success = false,
                    message = ex.message,
                    timestamp = LocalDateTime.now(),
                    path = request.requestURI,
                    data = buildMap {
                        put("oracleCode", ex.oracleCode)
                        if (ex.sqlState != null) put("sqlState", ex.sqlState)
                        if (ex.businessErrors != null) {
                            put("businessErrors", ex.businessErrors)
                        }
                        if (ex.technicalErrors != null) {
                            put("technicalErrors", ex.technicalErrors)
                        }
                        if (ex.details != null && isDevelopment()) put("details", ex.details)
                    }
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): MyApiResponse<Any> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        return MyApiResponse.unsuccess("Ошибка валидации: $errors", null)
    }

    // Кастомное исключение для токенов
    class InvalidTokenException(message: String?) : RuntimeException(message)
}