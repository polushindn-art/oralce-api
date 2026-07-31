package com.example.oracleapi.repository.glossary

import com.example.oracleapi.entity.table.GlossaryHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GlossaryHistoryRepository : JpaRepository<GlossaryHistoryEntity, Long> {

    @Query("""
        SELECT h FROM GlossaryHistoryEntity h 
        WHERE h.termRn = :termRn 
        ORDER BY h.version DESC
    """)
    fun findByTermRnOrderByVersionDesc(@Param("termRn") termRn: Long): List<GlossaryHistoryEntity>

    @Query("""
        SELECT MAX(h.version) FROM GlossaryHistoryEntity h 
        WHERE h.termRn = :termRn
    """)
    fun findMaxVersionByTermRn(@Param("termRn") termRn: Long): Int?

    @Query("""
        SELECT h FROM GlossaryHistoryEntity h 
        WHERE h.termRn = :termRn AND h.version = :version
    """)
    fun findByTermRnAndVersion(
        @Param("termRn") termRn: Long,
        @Param("version") version: Int
    ): GlossaryHistoryEntity?

    fun deleteByTermRn(@Param("termRn") termRn: Long)
}