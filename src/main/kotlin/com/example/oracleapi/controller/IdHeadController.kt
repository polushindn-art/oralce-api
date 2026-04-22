package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.common.PageResponse
import com.example.oracleapi.dto.idhead.IdheadResponse
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
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/idhead")
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
                content = [Content(schema = Schema(implementation = MyApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Ошибка валидации",
                content = [Content(schema = Schema(implementation = MyApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "500",
                description = "Ошибка при создании документа",
                content = [Content(schema = Schema(implementation = MyApiResponse::class))]
            )
        ]
    )
    fun createPrihod(
        @Valid @RequestBody request: PrihodRequest,
        httpRequest: HttpServletRequest
    ): MyApiResponse<PrihodResponse> {

        val rn = idHeadService.prihodCreate(request)

        return MyApiResponse.success(
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
    ): MyApiResponse<StatusUpdateResponse> {

        idHeadService.updateStatus(request)

        return MyApiResponse.success(
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
    ): MyApiResponse<IdHeadDeleteResponse> {
        idHeadService.delete(request)
        return MyApiResponse.success(
            data = IdHeadDeleteResponse(
                success = true,
                message = "Документ успешно удален",
                rn = request.rn
            ),
            message = "Документ удален",
            path = httpRequest.requestURI
        )
    }

    @GetMapping("/status/{status}")
    fun getByStatus(@PathVariable status: Long, httpRequest: HttpServletRequest): ResponseEntity<MyApiResponse<List<IdheadResponse>>> {
        val list = idHeadService.getByStatus(status)
        return ResponseEntity.ok(
            MyApiResponse.success(
                data = list,
                message = "Найдено ${list.size} документов со статусом $status",
                path = httpRequest.requestURI
            )
        )
    }

    @GetMapping("/count")
    fun getCount(): ResponseEntity<Map<String, Long>> {
        val total =  idHeadService.getCount()
        val status0 = idHeadService.getByStatus(0).size.toLong()
        return ResponseEntity.ok(mapOf("total" to total, "status0" to status0))
    }

    @GetMapping("/filter")
    fun getByFilters(
        @RequestParam(required = false) status: Long?,
        @RequestParam(required = false) doctype: String?,
        httpRequest: HttpServletRequest
    ): ResponseEntity<MyApiResponse<List<IdheadResponse>>> {
        val list = idHeadService.getByFilters(status, doctype)
        return ResponseEntity.ok(
            MyApiResponse.success(
                data = list,
                message = "Найдено ${list.size} документов",
                path = httpRequest.requestURI
            )
        )
    }

    @GetMapping("/{rn}/tsd")
    @Operation(
        summary = "Получить документ со спецификациями для ТСД",
        description = "Возвращает заголовок документа и все строки спецификации с данными номенклатуры"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Документ найден"),
            SwaggerApiResponse(responseCode = "404", description = "Документ не найден")
        ]
    )
    fun getDocumentWithSpecs(
        @PathVariable rn: Long,
        httpRequest: HttpServletRequest
    ): ResponseEntity<MyApiResponse<com.example.oracleapi.dto.idspec.IdheadWithSpecTsdResponse>> {
        val result = idHeadService.getDocumentWithSpecs(rn)
        return ResponseEntity.ok(
            MyApiResponse.success(
                data = result,
                message = "Документ ${result.docpref}${result.docnumb} от ${result.docdate}, позиций: ${result.specs.size}",
                path = httpRequest.requestURI
            )
        )
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все документы с пагинацией")
    fun getAllWithPagination(
        @PageableDefault(size = 20, sort = ["docdate"], direction = Sort.Direction.DESC) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<MyApiResponse<PageResponse<IdheadResponse>>> {
        val result = idHeadService.getAllWithPagination(pageable)
        return ResponseEntity.ok(
            MyApiResponse.success(
                data = result,
                message = "Найдено ${result.totalElements} документов, показано ${result.content.size}",
                path = httpRequest.requestURI
            )
        )
    }

    @GetMapping("/status/{status}/page")
    @Operation(summary = "Получить документы по статусу с пагинацией")
    fun getByStatusWithPagination(
        @PathVariable status: Long,
        @PageableDefault(size = 20, sort = ["docdate"], direction = Sort.Direction.DESC) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<MyApiResponse<PageResponse<IdheadResponse>>> {
        val result = idHeadService.getByStatusWithPagination(status, pageable)
        return ResponseEntity.ok(
            MyApiResponse.success(
                data = result,
                message = "Найдено ${result.totalElements} документов со статусом $status",
                path = httpRequest.requestURI
            )
        )
    }

    @GetMapping("/filter/page")
    @Operation(summary = "Фильтрация документов с пагинацией")
    fun getByFiltersWithPagination(
        @RequestParam(required = false) status: Long?,
        @RequestParam(required = false) doccode: String?,
        @PageableDefault(size = 20, sort = ["docdate"], direction = Sort.Direction.DESC) pageable: Pageable,
        httpRequest: HttpServletRequest
    ): ResponseEntity<MyApiResponse<PageResponse<IdheadResponse>>> {
        val result = idHeadService.getByFiltersWithPagination(status, doccode, pageable)
        return ResponseEntity.ok(
            MyApiResponse.success(
                data = result,
                message = "Найдено ${result.totalElements} документов",
                path = httpRequest.requestURI
            )
        )
    }

}