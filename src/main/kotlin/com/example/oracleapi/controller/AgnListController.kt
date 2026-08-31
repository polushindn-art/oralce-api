package com.example.oracleapi.controller

import com.example.oracleapi.dto.RnResponse
import com.example.oracleapi.dto.agn.AgnListForUpdResponse
import com.example.oracleapi.dto.agn.AgnListInfoResponse
import com.example.oracleapi.dto.agn.AgnListInsResponse
import com.example.oracleapi.dto.agn.AgnListUpdRequest
import com.example.oracleapi.dto.agn.AgnListUpdResponse
import com.example.oracleapi.dto.agnlist.AgnListInsRequest
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.agnList.AgnListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * Контроллер для управления справочником контрагентов (`AgnList`).
 * Обеспечивает операции создания, обновления, получения и удаления записей о контрагентах.
 */
@RestController
@RequestMapping("/v1/agnlist")
@Tag(name = "Контрагенты")
class AgnListController(
    private val agnListService: AgnListService
) : BaseController() {

    /**
     * Создает новую запись контрагента в таблице `AgnList`.
     *
     * @param request Объект запроса [AgnListInsRequest] с данными для создания контрагента.
     * @return [MyApiResponse] с результатом операции [AgnListInsResponse].
     */
    @PostMapping("/insert")
    @Operation(
        description = "Создает запись в таблице AgnList",
        summary = "Создать контрагента"
    )
    fun ins(
        @Valid @RequestBody request: AgnListInsRequest
    ): MyApiResponse<AgnListInsResponse> {
        return success(agnListService.ins(request))
    }

    /**
     * Обновляет существующую запись контрагента в таблице `AgnList`.
     *
     * @param request Объект запроса [AgnListUpdRequest] с обновленными данными.
     * @return [MyApiResponse] с результатом операции [AgnListUpdResponse].
     */
    @PutMapping("/update")
    @Operation(
        description = "Обновляет запись в таблице AgnList",
        summary = "Обновить контрагента"
    )
    fun upd(
        @Valid @RequestBody request: AgnListUpdRequest
    ): MyApiResponse<AgnListUpdResponse> {
        val response = agnListService.und(request)
        return success(response)
    }

    /**
     * Возвращает данные контрагента для редактирования по его регистрационному номеру (`RN`).
     *
     * @param rn Уникальный регистрационный номер контрагента.
     * @return [MyApiResponse] содержащий [AgnListForUpdResponse].
     */
    @GetMapping("/get")
    @Operation(
        description = "Возвращает запись из таблицы AgnList по RN",
        summary = "Получить контрагента"
    )
    fun getAgn(@Valid rn: Long): MyApiResponse<AgnListForUpdResponse> {
        return success(agnListService.getByRnForUpdate(rn))
    }

    /**
     * Удаляет запись контрагента из таблицы `AgnList` по регистрационному номеру (`RN`).
     *
     * @param rn Уникальный регистрационный номер удаляемого контрагента.
     * @return [MyApiResponse] с информацией об удаленном `RN` ([RnResponse]).
     */
    @DeleteMapping("/del")
    @Operation(
        description = "Удаляет запись из таблицы AgnList",
        summary = "Удалить запись"
    )
    fun delete(@Valid rn: Long): MyApiResponse<RnResponse> {
        return success(agnListService.del(rn))
    }

    /**
     * Получить информацию о контрагенте
     * @param rn Идентификатор
     * @return [com.example.oracleapi.dto.agn.AgnListInfoResponse]
     * */
    @GetMapping("info")
    @Operation(
        summary = "Получить информацию"
    )
    fun getInfo(@Valid rn: Long): MyApiResponse<AgnListInfoResponse> {
        return success(agnListService.getByRnInfo(rn))
    }

}