package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.paydoc.PaydocDto
import com.example.oracleapi.service.paydoc.PayDocService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/paydoc")
@Tag(name = "Платеждные документы", description = "Paydoc")
class PayDocController(
    private val payDocService: PayDocService
) : BaseController() {

    @GetMapping("/getRn/{rn}")
    fun getByRn(
        @PathVariable rn: Long
    ): MyApiResponse<List<PaydocDto>> {
        return successList(payDocService.getByRn(rn))
    }

}