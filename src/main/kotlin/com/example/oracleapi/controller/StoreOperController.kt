package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.storeoper.StoreoperDto
import com.example.oracleapi.service.storeoper.StoreOperService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Контроллер для работы со складскими операциями
 * */
@RestController
@RequestMapping("/v1/storeoper")
@Tag(
    name = "Складские операции"
)
class StoreOperController(
    private val storeOperService: StoreOperService
) : BaseController() {

    /**
     * Получить все складсике операции
     * @return Список операций [StoreoperDto]
     * */
    @GetMapping("/all")
    @Operation(summary = "Получить все операции")
    fun getAll(): MyApiResponse<List<StoreoperDto>> {
        return successList(storeOperService.allRecords())
    }

}