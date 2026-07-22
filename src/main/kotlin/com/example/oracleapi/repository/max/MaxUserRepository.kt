package com.example.oracleapi.repository.max

import com.example.oracleapi.entity.table.MaxUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MaxUserRepository : JpaRepository<MaxUser, Long> {

    fun findByInternalNumber(internalNumber: String): MaxUser?

    fun findByUserId(userId: String): MaxUser?

}