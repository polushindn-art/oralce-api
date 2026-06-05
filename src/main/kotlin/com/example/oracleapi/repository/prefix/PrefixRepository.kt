package com.example.oracleapi.repository.prefix

import com.example.oracleapi.entity.table.Prefix
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PrefixRepository : JpaRepository<Prefix, Long> {
    fun existsByDocpref(prefix: String): Boolean
    fun findByDivisionEntity_Divisioncode(divisionCode: String, pageable: Pageable): Page<Prefix>
}