package com.example.oracleapi.repository.store

import com.example.oracleapi.dto.tsdlist.StoreInfo
import com.example.oracleapi.entity.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository : JpaRepository<Store, Long> {
    fun existsByRn(rn: Long): Boolean
    fun findByStorepbe(pbeRn: Long): List<StoreInfo>

    // С фильтром по note = "#ТСД"
    fun findByStorepbeAndNote(pbeRn: Long, note: String): List<StoreInfo>
}