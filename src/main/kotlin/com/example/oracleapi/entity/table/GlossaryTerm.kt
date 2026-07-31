package com.example.oracleapi.entity.table
import com.example.oracleapi.Helper
import com.example.oracleapi.dto.glossary.SaveGlossaryRequest
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "glossary_terms", schema = Helper.SCHEME)
class GlossaryTermEntity {

    @Id
    @Column(name = "rn", nullable = false, precision = 17)
    var rn: Long? = null

    @Column(name = "term", nullable = false, length = 255, unique = true)
    var term: String? = null

    @Lob
    @Column(name = "definition", nullable = false, columnDefinition = "CLOB")
    var definition: String? = null

    @Column(name = "category", length = 100)
    var category: String? = null

    @Column(name = "sort_order")
    var sortOrder: Int = 0

    @Column(name = "created_by", length = 100)
    var createdBy: String? = null

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null

    @Column(name = "updated_by", length = 100)
    var updatedBy: String? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null

    @Column(name = "version")
    var version: Int = 1

    @Column(name = "is_active", length = 1)
    var isActive: String = "Y"

    // ============ Конструкторы ============

    constructor() {}

    constructor(
        rn: Long,
        term: String,
        definition: String,
        category: String? = null,
        sortOrder: Int = 0
    ) {
        this.rn = rn
        this.term = term
        this.definition = definition
        this.category = category
        this.sortOrder = sortOrder
        this.isActive = "Y"
        this.version = 1
    }

    // ============ Методы ============

    fun isActive(): Boolean = isActive == "Y"

    fun markDeleted() {
        this.isActive = "N"
    }

    fun incrementVersion() {
        this.version += 1
    }

    fun updateFrom(request: SaveGlossaryRequest) {
        this.term = request.term
        this.definition = request.definition
        this.category = request.category
        incrementVersion()
    }

    fun restore() {
        this.isActive = "Y"
        this.updatedAt = LocalDateTime.now()
        this.version += 1
    }

}