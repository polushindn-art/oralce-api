package com.example.oracleapi.repository.tsd

import com.example.oracleapi.handler.com.example.oracleapi.entity.tsd.TsdList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TsdListRepository : JpaRepository<TsdList, Long> {
    fun existsBySn(sn: String): Boolean
}