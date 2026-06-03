package com.example.oracleapi.controller

import com.example.oracleapi.config.JwtConfigProperties
import com.example.oracleapi.config.JwtHelper
import com.example.oracleapi.dto.*
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.common.MyApiResponse.Companion.unsuccess
import com.example.oracleapi.service.OracleAuthService
import com.example.oracleapi.service.tsdlist.TsdListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val log = LoggerFactory.getLogger(AuthController::class.java)

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Авторизация", description = "Контроллер авторизации")
class AuthController(
    private val jwtHelper: JwtHelper,
    private val jwtConfig: JwtConfigProperties,
    private val tsdListService: TsdListService,
    private val oracleAuthService: OracleAuthService
) : BaseController() {

    @PostMapping("/token")
    @Operation(summary = "Получить токен", description = "Получение токена авторизации")
    fun login(
        @RequestBody credentials: LoginCredentials,
        response: HttpServletResponse
    ): ResponseEntity<MyApiResponse<OracleAuthService.ResultAuth>> {

        log.info("Login attempt for user: {}", credentials.username)

        // Валидация
        if (credentials.username.isBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(unsuccess(message = "Имя пользователя обязательно"))
        }

        if (credentials.password.isBlank()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(unsuccess(message = "Пароль обязателен"))
        }

        // Аутентификация
        val authResult = oracleAuthService.authenticate(credentials.username, credentials.password)

        if (!authResult.state) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    unsuccess(
                        "Ошибка авторизации",
                        authResult
                    )
                )
        }

        // Cookie
        addAuthCookie(response, authResult.token!!)
        log.info("User {} authenticated successfully", credentials.username)

        return ResponseEntity.ok(
            success(
                authResult,
                message = "Авторизация успешна"
            )
        )
    }

    @GetMapping("/token/by-id")
    @Operation(summary = "Получить токен по Device ID терминала")
    fun loginByDeviceId(
        @RequestParam deviceId: String,
        response: HttpServletResponse
    ): ResponseEntity<MyApiResponse<AuthResponse>> {

        val terminal = tsdListService.getTerminalByDeviceId(deviceId) ?: return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                error("Терминал с Device ID=$deviceId не найден")
            )

        if (!tsdListService.isTerminalActiveByDeviceId(deviceId)) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error("Терминал неактивен. Активная сессия не найдена"))
        }

        val userInfo = tsdListService.getUserByTerminalDeviceId(deviceId) ?: return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                error("Для данного терминала не найден активный пользователь")
            )

        val terminalFullInfo = tsdListService.getTerminalFullInfoAsRegisteredjson(deviceId)
        val token = jwtHelper.createToken(userInfo.usercode)
        addAuthCookie(response, token)
        log.info("Terminal {} authenticated as user {}", deviceId, userInfo.usercode)

        val authResponse = AuthResponse(
            device = DeviceInfo(deviceId = deviceId, sn = terminal.sn ?: ""),
            user = UserAuthInfo(
                rn = userInfo.rn,
                usercode = userInfo.usercode,
                username = userInfo.username,
                parole = userInfo.parole,
                userAgn = userInfo.userAgn,
                dscbarnumb = userInfo.dscbarnumb,
                roles = userInfo.parts
            ),
            terminal = terminalFullInfo?.let {
                TerminalAuthInfo(
                    timestart = it.timestart,
                    pbecode = it.pbecode,
                    pbern = it.pbern,
                    stores = it.store ?: emptyList(),
                    params = it.param?.map { param ->
                        ParamBriefInfo(
                            name = param.name,
                            value = param.value,
                            description = param.description
                        )
                    } ?: emptyList()
                )
            },
            token = token,
            expiresIn = jwtConfig.expiration
        )

        return ResponseEntity.ok(
            success(
                data = authResponse,
                message = "Терминал авторизован по Device ID"
            )
        )
    }

    @PostMapping("/logout")
    @Operation(summary = "Завершить сеанс")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<MyApiResponse<Map<String, String>>> {

        removeAuthCookie(response)
        log.info("User logged out")

        return ResponseEntity.ok(
            MyApiResponse.success(
                data = mapOf("message" to "Вы вышли из системы"),
                message = "Выход выполнен",
                path = request.requestURI
            )
        )
    }

    @GetMapping
    @Operation(summary = "Ссылка на Beta")
    fun tsd():String {
        return "https://www.rustore.ru/catalog/app/com.example.qshop?testingSubscription=beta"
    }

    // ========== PRIVATE HELPERS ==========

    private fun addAuthCookie(response: HttpServletResponse, token: String) {
        val cookie = Cookie(jwtConfig.cookieName, token).apply {
            isHttpOnly = true
            secure = jwtConfig.secure
            path = "/"
            maxAge = jwtConfig.cookieMaxAgeSeconds
        }
        response.addCookie(cookie)
    }

    private fun removeAuthCookie(response: HttpServletResponse) {
        val cookie = Cookie(jwtConfig.cookieName, "").apply {
            maxAge = 0
            path = "/"
            isHttpOnly = true
            secure = jwtConfig.secure
        }
        response.addCookie(cookie)
    }

    data class LoginCredentials(
        val username: String,
        val password: String
    )
}