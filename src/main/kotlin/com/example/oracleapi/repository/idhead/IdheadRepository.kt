package com.example.oracleapi.repository.idhead

import com.example.oracleapi.entity.idhead.Idhead
import org.springframework.data.jpa.repository.JpaRepository

interface IdheadRepository : JpaRepository<Idhead, Long> {
    fun existsByRn(rn: Long): Boolean
}