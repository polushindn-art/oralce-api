package com.example.oracleapi.controller

import com.example.oracleapi.dto.RequestRN
import com.example.oracleapi.dto.contract.ContractResponse
import com.example.oracleapi.service.contract.ContractService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/contract")
@Tag(name = "Договор")
class ContractController(private val contractService: ContractService) : BaseController() {
    @PostMapping("get_contract")
    fun getContract(@Valid @RequestBody request: RequestRN): List<ContractResponse> {
        return contractService.getContract(request)
    }
}