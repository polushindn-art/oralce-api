package com.example.oracleapi.service.contract

import com.example.oracleapi.dto.RequestRN
import com.example.oracleapi.dto.contract.ContractResponse
import com.example.oracleapi.repository.contract.ContractRepository
import org.springframework.stereotype.Service

@Service
class ContractService(
    private val contractRepository: ContractRepository
) {
    fun getContract(request: RequestRN): List<ContractResponse> {
        return contractRepository.findContractByAgnlist(request.rn!!).map { ContractResponse.fromEntity(it) }
    }
}
