package com.example.oracleapi.repository.store

import com.example.oracleapi.dto.store.StoreResponse
import com.example.oracleapi.entity.Store
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoreRepository : JpaRepository<Store, Long> {

    fun findAllByOrderByStorecodeAsc(): List<Store>

    fun existsByRn(rn: Long): Boolean

    fun findByStorepbe(pbeRn: Long): List<Store>

    fun findByStorepbeAndNote(pbeRn: Long, note: String): List<Store>

    fun findByStorenameContainingIgnoreCaseOrStorecodeContainingIgnoreCase(
        storename: String,
        storecode: String,
        pageable: Pageable
    ): Page<Store>

    fun findByStorenameContainingIgnoreCase(storename: String, pageable: Pageable): Page<Store>

    fun findByStorecodeContainingIgnoreCase(storecode: String, pageable: Pageable): Page<Store>
}