package com.example.oracleapi.controller

import com.example.oracleapi.config.JwtConfigProperties
import com.example.oracleapi.config.JwtHelper
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = LoggerFactory.getLogger(AuthController::class.java)

@RestController
@RequestMapping("/auth")
@Tag(name = "auth", description = "Контроллер авторизации")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtHelper: JwtHelper,
    private val jwtConfig: JwtConfigProperties,
    private val tsdListService: TsdListService  // для проверки терминалов
) {
    @PostMapping("/token")
    @Operation(
        summary = "Получить токен",
        description = "Получение токена авторизации и загрузка cookie с токеном"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Успешная авторизация"),
            ApiResponse(responseCode = "400", description = "Неверные параметры запроса", content = [Content()]),
            ApiResponse(responseCode = "401", description = "Неверные логин или пароль", content = [Content()])
        ]
    )
    fun login(@RequestBody credentials: LoginCredentials, response: HttpServletResponse): ResponseEntity<*> {

        if (credentials.username.isBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Имя пользователя обязательно"))
        }
        if (credentials.password.isBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Пароль обязателен"))
        }

        try {
            val authToken = UsernamePasswordAuthenticationToken(credentials.username, credentials.password)
            authenticationManager.authenticate(authToken)

            val token = jwtHelper.createToken(credentials.username)
            log.info("User {} authenticated successfully", credentials.username)

            // Используем параметры из конфига!
            val cookie = Cookie(jwtConfig.cookieName, token).apply {
                isHttpOnly = true
                secure = jwtConfig.secure  // Берем из конфига!
                path = "/"
                maxAge = jwtConfig.cookieMaxAgeSeconds  // Берем из конфига!
            }

            response.addCookie(cookie)

            return ResponseEntity.ok(
                mapOf(
                    "message" to "Авторизация успешна",
                    "username" to credentials.username,
                    "token" to token,
                    "expiresIn" to jwtConfig.expiration  // Можно вернуть клиенту
                )
            )

        } catch (_: BadCredentialsException) {
            log.warn("Invalid login attempt for user: {}", credentials.username)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Неверные логин или пароль"))
        } catch (ex: Exception) {
            log.error("Authentication error", ex)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Внутренняя ошибка сервера"))
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
            ApiResponse(responseCode = "200", description = "Терминал авторизован"),
            ApiResponse(responseCode = "400", description = "Серийный номер обязателен"),
            ApiResponse(responseCode = "401", description = "Терминал не зарегистрирован"),
            ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
        ]
    )
    fun terminalLogin(@RequestParam(required = false) sn: String?, response: HttpServletResponse): ResponseEntity<*> {

        if (sn.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "Серийный номер обязателен"))
        }

        try {
            // Получаем информацию о пользователе терминала
            val userInfo = tsdListService.getUserByTerminalSn(sn)

            if (userInfo == null) {
                log.warn("Terminal with SN {} is not registered or has no active user", sn)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                        mapOf(
                            "error" to "ТСД не зарегистрирован",
                            "message" to "Для данного терминала не найдена активная сессия",
                            "sn" to sn
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
                mapOf(
                    "message" to "Терминал авторизован",
                    "sn" to sn,
                    "usercode" to userInfo.usercode,
                    "username" to userInfo.username,
                    "token" to token,
                    "expiresIn" to jwtConfig.expiration
                )
            )

        } catch (ex: Exception) {
            log.error("Terminal authentication error for SN: {}", sn, ex)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Внутренняя ошибка сервера"))
        }
    }


    @Operation(summary = "Завершить сеанс", description = "Завершить сеанс и удалить cookie с токеном")
    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<*> {

        val expiredCookie = Cookie(jwtConfig.cookieName, "").apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
            secure = jwtConfig.secure  // Берем из конфига!
        }
        response.addCookie(expiredCookie)

        log.info("User logged out successfully")
        return ResponseEntity.ok(mapOf("message" to "Вы вышли из системы"))
    }

    data class LoginCredentials(
        val username: String,
        val password: String
    )

}