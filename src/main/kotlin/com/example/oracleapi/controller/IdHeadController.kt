package com.example.oracleapi.controller

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.idhead.IdheadResponse
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteRequest
import com.example.oracleapi.dto.idhead.del.IdHeadDeleteResponse
import com.example.oracleapi.dto.idhead.prihod.PrihodRequest
import com.example.oracleapi.dto.idhead.prihod.PrihodResponse
import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import com.example.oracleapi.dto.idhead.status.StatusUpdateResponse
import com.example.oracleapi.service.field.FieldService
import com.example.oracleapi.service.idhead.IdHeadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequestMapping("/v1/idhead")
@Tag(name = "Складскте документы", description = "Пакет для работы со складскими документами")
class IdHeadController(
    private val idHeadService: IdHeadService,
    private val fieldService: FieldService
) : BaseController() {

    @PostMapping("/create")
    @Operation(
        summary = "Создать приходный ордер",
        description = "Создает приходный ордер на основе JSON данных. " +
                "Процедура: QREAL.CREATE_PRIHORD_BY_JSON"
    )
    fun createPrihod(
        @Valid @RequestBody request: PrihodRequest
    ): MyApiResponse<PrihodResponse> {

        val rn = idHeadService.prihodCreate(request)

        return success(
            PrihodResponse(
                success = true,
                message = "Приходный ордер успешно создан",
                idheadRn = rn
            )
        )
    }

    @PutMapping("/status")
    @Operation(
        summary = "Обновить статус документа",
        description = "Обновляет статус документа в таблице IDHEAD через процедуру PKG_IDHEAD.STATUS_UPDATE"
    )
    fun updateStatus(
        @Valid @RequestBody request: StatusUpdateRequest
    ): MyApiResponse<StatusUpdateResponse> {

        idHeadService.updateStatus(request)

        return success(
            data = StatusUpdateResponse(
                success = true,
                message = "Статус документа успешно обновлен",
                rn = request.rn,
                newStatus = request.newStatus
            )
        )
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Удалить документ",
        description = "Удаляет документ через процедуру PKG_IDHEAD.DEL"
    )
    fun delete(
        @Valid @RequestBody request: IdHeadDeleteRequest
    ): MyApiResponse<IdHeadDeleteResponse> {
        idHeadService.delete(request)
        return success(
            data = IdHeadDeleteResponse(
                success = true,
                message = "Документ успешно удален",
                rn = request.rn
            )
        )
    }

    @GetMapping("/status/{status}")
    @Operation(
        summary = "Получить документы по статусу"
    )
    fun getByStatus(@PathVariable status: Long): MyApiResponse<List<IdheadResponse>> {
        return successList(idHeadService.getByStatus(status))
    }

    @GetMapping("/count")
    @Operation(
        summary = "Количество документов всего"
    )
    fun getCount(): MyApiResponse<Map<String, Long>> {
        val total = idHeadService.getCount()
        return success(mapOf("total" to total))
    }

    @GetMapping("/countByStatus/{status}")
    @Operation(
        summary = "Количество документов по статусу"
    )
    fun getCountByStatus(
        @PathVariable status: Long,
    ): MyApiResponse<Map<String, Any>> {
        val total = idHeadService.getCount()
        val statusCount = idHeadService.getCountByStatus(status)
        val statusCode = fieldService.getFieldValue(Helper.IDSTATUS, status).fieldComment
        return success(
            mapOf(
                "total" to total,
                "status" to status,
                "statusName" to statusCode,
                "count" to statusCount
            )
        )
    }

    @GetMapping("/{rn}/tsd")
    @Operation(
        summary = "Получить документ со спецификацией",
        description = "Возвращает заголовок документа и все строки спецификации с данными номенклатуры"
    )
    fun getDocumentWithSpecs(
        @PathVariable rn: Long
    ): MyApiResponse<com.example.oracleapi.dto.idspec.IdheadWithSpecTsdResponse> {
        return success(idHeadService.getDocumentWithSpecs(rn))
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все документы с пагинацией")
    fun getAllWithPagination(
        @PageableDefault(size = 20, sort = ["docdate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): MyApiResponse<List<IdheadResponse>> {
        return success(idHeadService.getAllWithPagination(pageable))

    }

    @GetMapping("/status/{status}/page")
    @Operation(summary = "Получить документы по статусу с пагинацией")
    fun getByStatusWithPagination(
        @PathVariable status: Long,
        @PageableDefault(size = 20, sort = ["docdate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): MyApiResponse<List<IdheadResponse>> {
        return success(idHeadService.getByStatusWithPagination(status, pageable))
    }

    @GetMapping("/filter/page")
    @Operation(summary = "Фильтрация документов с пагинацией")
    fun getByFiltersWithPagination(
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) doctype: Long?,
        @RequestParam(required = false) docnumb: BigDecimal?,
        @RequestParam(required = false) storein: Long?,
        @RequestParam(required = false) storeout: Long?,
        @RequestParam(required = false) dateFrom: LocalDate?,
        @RequestParam(required = false) dateTo: LocalDate?,
        @PageableDefault(size = 20, sort = ["docdate", "rn"], direction = Sort.Direction.DESC) pageable: Pageable
    ): MyApiResponse<List<IdheadResponse>> {
        return success(idHeadService.getByFiltersWithPagination(status, doctype, docnumb, storein, storeout, dateFrom, dateTo, pageable))
    }

}