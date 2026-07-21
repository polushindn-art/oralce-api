package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.max.AgeVerificationRequest
import com.example.oracleapi.dto.max.AgeVerificationResponse
import com.example.oracleapi.service.max.MaxVerificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/max/verify")
@Tag(name = "MAX Цифровой ID", description = "Интеграция с Цифровым ID MAX")
class MaxController(
    private val verificationService: MaxVerificationService
) : BaseController() {

    @PostMapping("/age")
    @Operation(summary = "Проверить возраст через Цифровой ID")
    fun verifyAge(
        @Valid @RequestBody request: AgeVerificationRequest
    ): MyApiResponse<AgeVerificationResponse> {
        val result = verificationService.verifyAge(request.sessionId)

        return if (result.error == null) {
            val message = if (result.isAdult) "Возраст подтвержден (18+)" else "Возраст не подтвержден"
            success(result, message)
        } else {
            error(result.error, result)
        }
    }
}