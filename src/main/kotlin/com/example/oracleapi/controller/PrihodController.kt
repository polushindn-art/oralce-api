package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.prihod.PrihodResponse
import com.example.oracleapi.dto.prihod.PrihodRequest
import com.example.oracleapi.service.prihord.PrihodService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/prihod")
@Tag(name = "Приходный ордер", description = "Создание приходного ордера по JSON")
class PrihodController(
    private val prihodService: PrihodService
) {

    @PostMapping("/create")
    @Operation(
        summary = "Создать приходный ордер",
        description = "Создает приходный ордер на основе JSON данных. " +
                "Процедура: QREAL.CREATE_PRIHORD_BY_JSON"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "Успешное создание",
                content = [Content(schema = Schema(implementation = ApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Ошибка валидации",
                content = [Content(schema = Schema(implementation = ApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "500",
                description = "Ошибка при создании документа",
                content = [Content(schema = Schema(implementation = ApiResponse::class))]
            )
        ]
    )
    fun createPrihod(
        @Valid @RequestBody request: PrihodRequest,
        httpRequest: HttpServletRequest
    ): ApiResponse<PrihodResponse> {

        val rn = prihodService.createPrihodByJson(request)

        return ApiResponse.success(
            data = PrihodResponse(
                success = true,
                message = "Приходный ордер успешно создан",
                idheadRn = rn
            ),
            message = "Документ создан",
            path = httpRequest.requestURI
        )
    }

}