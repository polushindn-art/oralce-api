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
@RequestMapping("/v1/email")
@Tag(name = "pkg_mail", description = "API для отправки email сообщений")
class EmailController(
    private val emailService: EmailService
) : BaseController() {

    @PostMapping("/send")
    @Operation(
        summary = "Отправить простое письмо",
        description = "Отправляет письмо через Oracle процедуру PKG_SENDMAIL.SIMPLE_TEXT"
    )
    fun sendSimpleEmail(
        @Valid @RequestBody request: SimpleEmailRequest
    ): MyApiResponse<Long> {
        return success(
            emailService.sendSimpleEmail(request),
            message = "Письмо успешно отправлено"
        )
    }
}