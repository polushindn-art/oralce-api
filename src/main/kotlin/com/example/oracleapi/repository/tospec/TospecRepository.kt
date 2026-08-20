package com.example.oracleapi.repository.tospec

import com.example.oracleapi.entity.table.Tospec
import org.springframework.data.jpa.repository.JpaRepository

interface TospecRepository : JpaRepository<Tospec, Long> {
    fun findByPrn(prn: Long): List<Tospec>
}