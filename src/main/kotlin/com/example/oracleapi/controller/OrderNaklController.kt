package com.example.oracleapi.controller

import com.example.oracleapi.dto.ResponseRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadDelRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklHeadRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklSpecRequest
import com.example.oracleapi.dto.orderNakl.OrderNaklSpecResponse
import com.example.oracleapi.service.ordernaklhead.NaklHeadServise
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/naklhead")
@Tag(name = "Документ Накладные расходы")
class OrderNaklController(
    private val naklHeadService: NaklHeadServise
) : BaseController() {

    @PostMapping("/ins_head")
    @Operation(summary = "Создать заголовок документа")
    fun insHead(@Valid @RequestBody request: OrderNaklHeadRequest): MyApiResponse<ResponseRN> {
        return success(naklHeadService.insNaklHead(request))
    }

    @PostMapping("/ins_spec")
    @Operation(summary = "Создать спецификацию")
    fun insSpec(@Valid @RequestBody request: OrderNaklSpecRequest): MyApiResponse<OrderNaklSpecResponse> {
        return success(naklHeadService.insNaklSpec(request))
    }

    @DeleteMapping("/del_head")
    @Operation(summary = "Удаление документа")
    fun delHead(@Valid @RequestBody request: OrderNaklHeadDelRequest): MyApiResponse<ResponseRN> {
        return success(naklHeadService.delHead(request))
    }

}