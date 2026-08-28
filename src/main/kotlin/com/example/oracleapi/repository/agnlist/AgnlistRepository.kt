package com.example.oracleapi.repository.agnlist

import com.example.oracleapi.entity.table.AgnList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AgnListRepository : JpaRepository<AgnList, Long> {

    fun findByRn(rn: Long): AgnList?
    fun existsByAgncode(agncode: String): Boolean
    fun findByRnAndAgntype(rn: Long, type: Long): AgnList?
    fun existsByRnAndAgntype(rn: Long, agntype: Long): Boolean
}