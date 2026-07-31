package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "glossary_history", schema = Helper.SCHEME)
class GlossaryHistoryEntity {

    @Id
    @Column(name = "rn", nullable = false, precision = 17)
    var rn: Long? = null

    @Column(name = "term_rn", nullable = false, precision = 17)
    var termRn: Long? = null

    @Column(name = "term", length = 255)
    var term: String? = null

    @Lob
    @Column(name = "definition", columnDefinition = "CLOB")
    var definition: String? = null

    @Column(name = "version")
    var version: Int = 1

    @Column(name = "changed_by", length = 100)
    var changedBy: String? = null

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    var changedAt: LocalDateTime? = null

    // ============ Конструкторы ============

    constructor()

    constructor(
        rn: Long,
        termRn: Long,
        term: String,
        definition: String,
        version: Int,
        changedBy: String? = null
    ) {
        this.rn = rn
        this.termRn = termRn
        this.term = term
        this.definition = definition
        this.version = version
        this.changedBy = changedBy
    }

    companion object {
        fun fromTerm(termEntity: GlossaryTermEntity): GlossaryHistoryEntity {
            return GlossaryHistoryEntity(
                rn = System.currentTimeMillis(),
                termRn = termEntity.rn ?: throw Exception("termRn не может быть null"),
                term = termEntity.term ?: "",
                definition = termEntity.definition ?: "",
                version = termEntity.version
            )
        }
    }
}