package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.sendmail.SimpleEmailRequest
import com.example.oracleapi.dto.sendmail.SimpleEmailResponse
import com.example.oracleapi.service.sendmail.EmailService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/email")
@Tag(name = "pkg_mail", description = "API для отправки email сообщений")
class EmailController(
    private val emailService: EmailService
) {

    @PostMapping("/send")
    @Operation(
        summary = "Отправить простое письмо",
        description = "Отправляет письмо через Oracle процедуру PKG_SENDMAIL.SIMPLE_TEXT"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Письмо успешно отправлено"),
            SwaggerApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            SwaggerApiResponse(responseCode = "500", description = "Ошибка сервера или ошибка отправки письма")
        ]
    )
    fun sendSimpleEmail(
        @Valid @RequestBody request: SimpleEmailRequest,
        httpRequest: HttpServletRequest
    ): MyApiResponse<SimpleEmailResponse> {

        val mailRn = emailService.sendSimpleEmail(request)

        return MyApiResponse.success(
            data = SimpleEmailResponse(
                success = true,
                message = "Письмо успешно отправлено",
                mailRn
            ),
            message = "Письмо отправлено",
            path = httpRequest.requestURI
        )
    }
}