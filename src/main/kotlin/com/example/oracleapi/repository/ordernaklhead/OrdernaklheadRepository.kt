package com.example.oracleapi.repository.ordernaklhead

import com.example.oracleapi.entity.table.Ordernaklhead
import org.springframework.data.jpa.repository.JpaRepository

interface OrdernaklheadRepository : JpaRepository<Ordernaklhead, Long> {
    fun existsByRn(rn: Long): Boolean
}