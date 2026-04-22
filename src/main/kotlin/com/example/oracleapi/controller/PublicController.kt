package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.public.GenIdResponse
import com.example.oracleapi.dto.public.GetNomenByBarcodeRequest
import com.example.oracleapi.dto.public.GetNomenByBarcodeResponse
import com.example.oracleapi.service.public.PublicProcedureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public")
@Tag(name = "pkg_public", description = "Процедуры пакета PKG_PUBLIC")
class PublicController(
    private val publicProcedureService: PublicProcedureService
) {
    @PostMapping("/getNomenByBarcode")
    @Operation(
        summary = "pkg_public.getnomenbybarcode",
        description = "Получить идентификатор номенклатуры по штрих-коду"
    )
    fun getNomenByBarcode(
        @Valid @RequestBody(required = true) request: GetNomenByBarcodeRequest,
        httpRequest: HttpServletRequest
    ): MyApiResponse<GetNomenByBarcodeResponse> {
        val result = publicProcedureService.getNomenByBarcode(request)
        return MyApiResponse.success(
            data = result,
            message = "Идентификатор получен",
            httpRequest.requestURI
        )
    }

    @GetMapping("/genIdRn")
    @Operation(
        summary = "pkg_public.genidrn",
        description = "Получить уникальный идентификатор RN"
    )
    fun genIdRn(
        httpRequest: HttpServletRequest
    ): MyApiResponse<GenIdResponse> {
        val result = publicProcedureService.getIdRn()
        return MyApiResponse.success(
            data = result,
            message = "Идентификатор RN получен",
            httpRequest.requestURI
        )
    }

    @GetMapping("/genIdRn/multiple")
    @Operation(summary = "Получить несколько RN", description = "Генерирует несколько уникальных идентификаторов")
    fun generateMultipleRn(
        @RequestParam(defaultValue = "3") count: Int,
        httpRequest: HttpServletRequest
    ): MyApiResponse<GenIdResponse> {
        val result = publicProcedureService.generateMultipleRn(count)
        return  MyApiResponse.success(
                        data = result,
                        message = "Сгенерировано $count идентификаторов",
                        httpRequest.requestURI
                    )
    }
}
