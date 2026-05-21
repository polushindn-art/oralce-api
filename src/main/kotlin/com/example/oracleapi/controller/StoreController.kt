package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.store.StoreResponse
import com.example.oracleapi.dto.store.StoreSimpleResponse
import com.example.oracleapi.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/stores")
@Tag(name = "Склады", description = "API для работы со складами")
class StoreController(
    private val storeService: StoreService
): BaseController() {

    @GetMapping("/all")
    @Operation(summary = "Получить все склады")
    fun getAllStores(): MyApiResponse<List<StoreResponse>> {
        return success(storeService.getAllSortedByStorecodeAsc())
    }

    @GetMapping("/pbe/{pbeRn}")
    @Operation(summary = "Получить склады по PBE")
    fun getStoresByPbe(
        @PathVariable pbeRn: Long
    ): MyApiResponse<List<StoreResponse>> {
        return success(storeService.getStoresByPbe(pbeRn))
    }

    @GetMapping("/tsd")
    @Operation(summary = "Получить склады для ТСД (с заметкой #ТСД)")
    fun getStoresForTsd(
        @RequestParam(required = true) pbeRn: Long
    ): MyApiResponse<List<StoreSimpleResponse>> {
        return successList(storeService.getStoresByPbeAndNote(pbeRn, "#ТСД"))
    }

    @GetMapping("/{rn}")
    @Operation(summary = "Получить склад по RN")
    fun getStoreByRn(
        @PathVariable rn: Long
    ): MyApiResponse<StoreResponse> {
        return success(storeService.getStoreByRn(rn))
    }
}