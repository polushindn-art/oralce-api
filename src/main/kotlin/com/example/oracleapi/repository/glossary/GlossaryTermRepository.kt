package com.example.oracleapi.repository.glossary

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.GlossaryTermEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface GlossaryTermRepository : JpaRepository<GlossaryTermEntity, Long> {

    // ============ FIND ============

    @Query("""
        SELECT t FROM GlossaryTermEntity t 
        WHERE t.isActive = 'Y' 
        ORDER BY t.sortOrder, t.term
    """)
    fun findAllActive(): List<GlossaryTermEntity>

    @Query("""
        SELECT t FROM GlossaryTermEntity t 
        WHERE t.isActive = 'Y' AND t.rn = :rn
    """)
    fun findActiveByRn(@Param("rn") rn: Long): GlossaryTermEntity?

    @Query("""
        SELECT t FROM GlossaryTermEntity t 
        WHERE t.isActive = 'Y' AND LOWER(t.term) = LOWER(:term)
    """)
    fun findActiveByTerm(@Param("term") term: String): GlossaryTermEntity?

    @Query("""
        SELECT t FROM GlossaryTermEntity t 
        WHERE t.isActive = 'Y' 
          AND t.category = :category 
        ORDER BY t.sortOrder, t.term
    """)
    fun findActiveByCategory(@Param("category") category: String): List<GlossaryTermEntity>

    @Query(
        value = """
        SELECT * FROM ${Helper.SCHEME}.glossary_terms 
        WHERE is_active = 'Y' 
          AND (LOWER(term) LIKE LOWER('%' || :query || '%')
           OR LOWER(definition) LIKE LOWER('%' || :query || '%'))
        ORDER BY sort_order, term
    """,
        nativeQuery = true
    )
    fun searchActive(@Param("query") query: String): List<GlossaryTermEntity>

    @Query("SELECT DISTINCT t.category FROM GlossaryTermEntity t WHERE t.isActive = 'Y' AND t.category IS NOT NULL ORDER BY t.category")
    fun findDistinctCategories(): List<String>

    // ============ CHECK ============

    @Query("""
        SELECT COUNT(t) > 0 FROM GlossaryTermEntity t 
        WHERE t.isActive = 'Y' AND LOWER(t.term) = LOWER(:term)
    """)
    fun existsByTermIgnoreCase(@Param("term") term: String): Boolean

    @Query("""
        SELECT COUNT(t) > 0 FROM GlossaryTermEntity t 
        WHERE t.isActive = 'Y' AND LOWER(t.term) = LOWER(:term) AND t.rn != :rn
    """)
    fun existsByTermIgnoreCaseAndRnNot(
        @Param("term") term: String,
        @Param("rn") rn: Long
    ): Boolean

    // ============ UPDATE ============

    @Modifying
    @Transactional
    @Query("""
        UPDATE GlossaryTermEntity t 
        SET t.isActive = 'N', t.updatedAt = CURRENT_TIMESTAMP 
        WHERE t.rn = :rn
    """)
    fun softDeleteByRn(@Param("rn") rn: Long): Int

    @Modifying
    @Transactional
    @Query("""
        UPDATE GlossaryTermEntity t 
        SET t.term = :term, t.definition = :definition, t.category = :category,
            t.updatedAt = CURRENT_TIMESTAMP, t.version = t.version + 1
        WHERE t.rn = :rn
    """)
    fun updateTerm(
        @Param("rn") rn: Long,
        @Param("term") term: String,
        @Param("definition") definition: String,
        @Param("category") category: String?
    ): Int

    @Query("""
    SELECT t FROM GlossaryTermEntity t 
    WHERE t.isActive = 'N' 
    ORDER BY t.updatedAt DESC
""")
    fun findAllDeleted(): List<GlossaryTermEntity>

    @Query("""
    SELECT t FROM GlossaryTermEntity t 
    WHERE t.isActive = 'N' AND t.rn = :rn
""")
    fun findDeletedByRn(@Param("rn") rn: Long): GlossaryTermEntity?

    @Query("SELECT t FROM GlossaryTermEntity t WHERE t.rn = :rn")
    fun findByRn(@Param("rn") rn: Long): GlossaryTermEntity?

}