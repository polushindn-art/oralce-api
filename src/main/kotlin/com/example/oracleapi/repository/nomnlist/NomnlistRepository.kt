package com.example.oracleapi.repository.nomnlist

import com.example.oracleapi.entity.table.Nomnlist
import org.springframework.data.jpa.repository.JpaRepository

interface NomnlistRepository : JpaRepository<Nomnlist, Long> {
    fun existsByRn(rn: Long): Boolean
    fun findByRn(rn: Long): Nomnlist?
}