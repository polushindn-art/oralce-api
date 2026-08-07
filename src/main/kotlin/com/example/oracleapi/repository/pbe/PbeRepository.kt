package com.example.oracleapi.repository.pbe

import com.example.oracleapi.entity.table.Pbe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PbeRepository: JpaRepository<Pbe, Long> {
    fun existsByRn(rn: Long): Boolean
    fun findByPbecode(pbecode: String?): Pbe?
}