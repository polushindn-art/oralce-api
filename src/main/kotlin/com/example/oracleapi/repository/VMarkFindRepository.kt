package com.example.oracleapi.repository

import com.example.oracleapi.entity.view.VMarkFind
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VMarkFindRepository: JpaRepository<VMarkFind, Long> {
    fun findByCis(cis: String): VMarkFind?
}