package com.example.oracleapi.repository.paydoc

import com.example.oracleapi.entity.table.Paydoc
import org.springframework.data.jpa.repository.JpaRepository

interface PaydocRepository : JpaRepository<Paydoc, Long> {
    fun findAllByRn(rn: Long): List<Paydoc>
}