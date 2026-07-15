package com.example.oracleapi.controller

import com.example.oracleapi.dto.RequestRN
import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.contract.ContractDto
import com.example.oracleapi.service.contract.ContractService
import com.example.oracleapi.service.idhead.UpdateStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/contract")
@Tag(name = "Договор")
class ContractController(private val contractService: ContractService) : BaseController() {

    @PostMapping("/get_contract_agnlist")
    @Operation(summary = "Получить договора для поставщика")
    fun getContract(@Valid @RequestBody request: RequestRN): MyApiResponse<List<ContractDto>> {
        return success(contractService.getContract(request))
    }

    @GetMapping("/filter/page")
    @Operation(summary = "Фильтрация документов с пагинацией")
    fun getBayFilterWithPagination(
        @RequestParam(required = false) agnlist: Long?,
        @RequestParam(required = false) ul: Long?,
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) status: Int?,
        @PageableDefault(size = 20, sort = ["begindate"], direction = Sort.Direction.DESC) pageable: Pageable
    ): MyApiResponse<List<ContractDto>> {
        return success(contractService.getByFielByPagination(agnlist, ul, limit, status, pageable))
    }

}