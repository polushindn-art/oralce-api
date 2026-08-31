package com.example.oracleapi.service.contract

import com.example.oracleapi.dto.RnRequest
import com.example.oracleapi.dto.contract.ContractDto
import com.example.oracleapi.entity.table.Contract
import com.example.oracleapi.repository.contract.ContractRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContractService(
    private val contractRepository: ContractRepository
) {

    @Transactional(readOnly = true)
    fun getContract(request: RnRequest): List<ContractDto> {
        val contract = contractRepository.findContractByAgnlist(request.rn!!)
        return contract.map { ContractDto.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getByFielByPagination(
        agn: Long?,
        ul: Long?,
        limited: Int?,
        status: Int?,
        pageable: Pageable
    ): Page<ContractDto> {
        val spec = buildSpecification(agn, ul, limited, status)
        return contractRepository.findAll(spec, pageable).map { ContractDto.fromEntity(it) }
    }

    private fun buildSpecification(
        agn: Long?,
        ul: Long?,
        limited: Int?,
        status: Int?
    ): Specification<Contract> {
        return Specification { root, _, builder ->
            val predicates = mutableListOf<Predicate>()

            agn?.let { predicates.add(builder.equal(root.get<Long>("agnlist"), it)) }
            ul?.let { predicates.add(builder.equal(root.get<Long>("ul"), it)) }
            limited?.let { predicates.add(builder.equal(root.get<Long>("limited"), it)) }
            status?.let { predicates.add(builder.equal(root.get<Long>("status"), it)) }

            builder.and(*predicates.toTypedArray())
        }
    }

}

