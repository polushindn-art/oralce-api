package com.example.oracleapi.repository.nomnlist

import com.example.oracleapi.entity.Nomnlist
import org.springframework.data.jpa.repository.JpaRepository

interface NomnlistRepository : JpaRepository<Nomnlist, Long> {
    fun existsByRn(rn: Long): Boolean
}