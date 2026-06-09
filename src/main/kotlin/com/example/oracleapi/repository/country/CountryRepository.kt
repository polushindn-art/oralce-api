package com.example.oracleapi.repository.country

import com.example.oracleapi.entity.table.Country
import org.springframework.data.jpa.repository.JpaRepository

interface CountryRepository : JpaRepository<Country, Long> {
    fun existsByRn(rn: Long): Boolean
}