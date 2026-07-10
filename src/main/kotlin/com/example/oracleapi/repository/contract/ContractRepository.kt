package com.example.oracleapi.repository.contract

import com.example.oracleapi.entity.table.Contract
import org.springframework.data.jpa.repository.JpaRepository

interface ContractRepository : JpaRepository<Contract, Long> {
    fun findContractByAgnlist(agnlist: Long): List<Contract>
}