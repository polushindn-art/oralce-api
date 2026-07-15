package com.example.oracleapi.repository.contract

import com.example.oracleapi.entity.table.Contract
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ContractRepository : JpaRepository<Contract, Long>, JpaSpecificationExecutor<Contract> {

    fun findContractByAgnlist(agnlist: Long): List<Contract>

    override fun findAll(pageable: Pageable): Page<Contract>

    fun getContractsByAgnlist(agnlist: Long): List<Contract>
}