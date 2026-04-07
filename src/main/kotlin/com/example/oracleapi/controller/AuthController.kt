package com.example.oracleapi.controller

import com.example.oracleapi.config.JwtConfigProperties
import com.example.oracleapi.config.JwtHelper
import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.repository.userlist.UserlistRepository
import com.example.oracleapi.service.tsdlist.TsdListService
import com.example.oracleapi.service.userlist.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

private val log = LoggerFactory.getLogger(AuthController::class.java)

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "Контроллер авторизации")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtHelper: JwtHelper,
    private val jwtConfig: JwtConfigProperties,
    private val tsdListService: TsdListService,
    private val userService: UserService
) {

    @PostMapping("/token")
    @Operation(
        summary = "Получить токен",
        description = "Получение токена авторизации и загрузка cookie с токеном"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Успешная авторизация"),
            SwaggerApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            SwaggerApiResponse(responseCode = "401", description = "Неверные логин или пароль")
        ]
    )
    fun login(
        @RequestBody credentials: LoginCredentials,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {

        log.info("Login attempt for user: {}", credentials.username)

        // Дополнительная проверка существования пользователя
        if (!userService.checkUserExists(credentials.username)) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    ApiResponse.error(
                        message = "Пользователь не найден",
                        path = request.requestURI
                    )
                )
        }

        if (credentials.username.isBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiResponse.error(
                        message = "Имя пользователя обязательно",
                        path = request.requestURI
                    )
                )
        }
        if (credentials.password.isBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiResponse.error(
                        message = "Пароль обязателен",
                        path = request.requestURI
                    )
                )
        }

        try {
            val authToken = UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
            authenticationManager.authenticate(authToken)

            val token = jwtHelper.createToken(credentials.username)
            log.info("User {} authenticated successfully", credentials.username)

            val cookie = Cookie(jwtConfig.cookieName, token).apply {
                isHttpOnly = true
                secure = jwtConfig.secure
                path = "/"
                maxAge = jwtConfig.cookieMaxAgeSeconds
            }
            response.addCookie(cookie)

            return ResponseEntity.ok(
                ApiResponse.success(
                    data = mapOf(
                        "username" to credentials.username,
                        "token" to token,
                        "expiresIn" to jwtConfig.expiration
                    ),
                    message = "Авторизация успешна",
                    path = request.requestURI
                )
            )

        } catch (_: BadCredentialsException) {
            log.warn("Invalid login attempt for user: {}", credentials.username)
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    ApiResponse.error(
                        message = "Неверные логин или пароль",
                        path = request.requestURI
                    )
                )
        } catch (ex: Exception) {
            log.error("Authentication error", ex)
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse.error(
                        message = "Внутренняя ошибка сервера: ${ex.message}",
                        path = request.requestURI
                    )
                )
        }
    }

    /**
     * Аутентификация терминала по SN - возвращает токен пользователя терминала
     */
    @GetMapping("/tsdtoken")
    @Operation(
        summary = "Получить токен пользователя по терминалу",
        description = "Получение токена авторизации для пользователя, работающего с терминалом"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Терминал авторизован"),
            SwaggerApiResponse(responseCode = "400", description = "Серийный номер обязателен"),
            SwaggerApiResponse(responseCode = "401", description = "Терминал не зарегистрирован"),
            SwaggerApiResponse(responseCode = "500", description = "Внутренняя ошибка")
        ]
    )
    fun terminalLogin(
        @RequestParam(required = false) sn: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {

        if (sn.isNullOrBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    ApiResponse.error(
                        message = "Серийный номер обязателен",
                        path = request.requestURI
                    )
                )
        }

        try {
            val userInfo = tsdListService.getUserByTerminalSn(sn)

            if (userInfo == null) {
                log.warn("Terminal with SN {} is not registered or has no active user", sn)
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                        ApiResponse.error(
                            message = "ТСД не зарегистрирован. Для данного терминала не найдена активная сессия",
                            path = request.requestURI
                        )
                    )
            }

            val token = jwtHelper.createToken(userInfo.usercode)
            log.info("Terminal {} authenticated as user {}", sn, userInfo.usercode)

            val cookie = Cookie(jwtConfig.cookieName, token).apply {
                isHttpOnly = true
                secure = jwtConfig.secure
                path = "/"
                maxAge = jwtConfig.cookieMaxAgeSeconds
            }
            response.addCookie(cookie)

            return ResponseEntity.ok(
                ApiResponse.success(
                    data = mapOf(
                        "sn" to sn,
                        "usercode" to userInfo.usercode,
                        "username" to userInfo.username,
                        "token" to token,
                        "expiresIn" to jwtConfig.expiration
                    ),
                    message = "Терминал авторизован",
                    path = request.requestURI
                )
            )

        } catch (ex: Exception) {
            log.error("Terminal authentication error for SN: {}", sn, ex)
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse.error(
                        message = "Внутренняя ошибка сервера: ${ex.message}",
                        path = request.requestURI
                    )
                )
        }
    }

    @Operation(summary = "Завершить сеанс", description = "Завершить сеанс и удалить cookie с токеном")
    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<Map<String, String>>> {

        val expiredCookie = Cookie(jwtConfig.cookieName, "").apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
            secure = jwtConfig.secure
        }
        response.addCookie(expiredCookie)

        log.info("User logged out successfully")

        return ResponseEntity.ok(
            ApiResponse.success(
                data = mapOf("message" to "Вы вышли из системы"),
                message = "Выход выполнен",
                path = request.requestURI
            )
        )
    }

    data class LoginCredentials(
        val username: String,
        val password: String
    )
}