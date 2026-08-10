package com.example.oracleapi.repository

import com.example.oracleapi.entity.table.ProtocolMail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProtocolMailRepository : JpaRepository<ProtocolMail, Long>, JpaSpecificationExecutor<ProtocolMail> {
    fun findByRn(rn: Long): ProtocolMail
}