package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.ApiResponse
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteResponse
import com.example.oracleapi.dto.idhead.prihod.PrihodResponse
import com.example.oracleapi.dto.idhead.prihod.PrihodRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateResponse
import com.example.oracleapi.service.idhead.IdHeadService
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
@Tag(name = "IdHead", description = "Пакет для работы со складскими документами")
class IdHeadController(
    private val idHeadService: IdHeadService
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

        val rn = idHeadService.prihodCreate(request)

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

    @PutMapping("/status")
    @Operation(
        summary = "Обновить статус документа",
        description = "Обновляет статус документа в таблице IDHEAD через процедуру PKG_IDHEAD.STATUS_UPDATE"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
            SwaggerApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            SwaggerApiResponse(responseCode = "500", description = "Ошибка при обновлении статуса")
        ]
    )
    fun updateStatus(
        @Valid @RequestBody request: StatusUpdateRequest,
        httpRequest: HttpServletRequest
    ): ApiResponse<StatusUpdateResponse> {

        idHeadService.updateStatus(request)

        return ApiResponse.success(
            data = StatusUpdateResponse(
                success = true,
                message = "Статус документа успешно обновлен",
                rn = request.rn,
                newStatus = request.newStatus
            ),
            message = "Статус обновлен",
            path = httpRequest.requestURI
        )
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Удалить документ",
        description = "Удаляет документ через процедуру PKG_IDHEAD.DEL"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Документ успешно удален"),
            SwaggerApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            SwaggerApiResponse(responseCode = "500", description = "Ошибка при удалении документа")
        ]
    )
    fun delete(
        @Valid @RequestBody request: IdHeadDeleteRequest,
        httpRequest: HttpServletRequest
    ): ApiResponse<IdHeadDeleteResponse> {
        idHeadService.delete(request)
        return ApiResponse.success(
            data = IdHeadDeleteResponse(
                success = true,
                message = "Документ успешно удален",
                rn = request.rn
            ),
            message = "Документ удален",
            path = httpRequest.requestURI
        )
    }

}