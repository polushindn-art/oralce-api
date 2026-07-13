package com.example.oracleapi.service.contract

import com.example.oracleapi.dto.RequestRN
import com.example.oracleapi.dto.contract.ContractDto
import com.example.oracleapi.repository.contract.ContractRepository
import org.springframework.stereotype.Service

@Service
class ContractService(
    private val contractRepository: ContractRepository
) {
    fun getContract(request: RequestRN): List<ContractDto> {
        val contract = contractRepository.findContractByAgnlist(request.rn!!)
        return contract.map { ContractDto.fromEntity(it) }
    }
}
