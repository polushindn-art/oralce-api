package com.example.oracleapi.repository.pbe

import com.example.oracleapi.entity.pbe.PBE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PbeRepository: JpaRepository<PBE, Long> {
    fun existsByRn(rn: Long): Boolean
}