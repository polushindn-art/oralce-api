package com.example.oracleapi.repository.tohead

import com.example.oracleapi.dto.tohead.ToheadDto
import com.example.oracleapi.entity.table.Tohead
import org.springframework.data.jpa.repository.JpaRepository

interface ToheadRepository : JpaRepository<Tohead, Long> {
    fun findToheadByRn(rn: Long): Tohead
}