package com.example.oracleapi.entity.view

import com.example.oracleapi.Helper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Immutable
@Table(name = "v_mark_find", schema = Helper.SCHEME)
open class VMarkFind {
    @Id
    @Column(name = "rn", nullable = false)
    var rn: Long? = null

    @Column(name = "DATE_ADD")
    var dateAdd: LocalDateTime? = null

    @Column(name = "km", nullable = false)
    var km: String? = null

    @Column(name = "JSON", columnDefinition = "CLOB")
    var json: String? = null

    @Column(name = "CIS")
    var cis: String? = null

    @Column(name = "GTIN")
    var gtin: String? = null

    @Column(name = "STATUS")
    var status: Long? = null

    @Column(name = "NOMEN")
    var nomen: Long? = null

    @Column(name = "STATE_MARK")
    var stateMark: LocalDate? = null

    @Column(name = "NOTE")
    var note: String? = null

    @Column(name = "ERR")
    var err: String? = null

    // ========================================================================
    // Вспомогательные методы для работы с JSON
    // ========================================================================

    companion object {
        private val objectMapper = ObjectMapper()
    }

    private fun getCisInfoNode(): JsonNode? {
        if (json.isNullOrBlank()) return null
        return try {
            val root = objectMapper.readTree(json)
            root.path(0).path("cisInfo").takeIf { !it.isMissingNode }
        } catch (_: Exception) {
            null
        }
    }


    fun getChildCis(): List<String>? {
        val childNode = getCisInfoNode()?.path("child")
        if (childNode == null || !childNode.isArray) return null
        val result = mutableListOf<String>()
        childNode.forEach { node ->
            node.asText()?.takeIf { it.isNotEmpty() }?.let { result.add(it) }
        }
        return result.takeIf { it.isNotEmpty() }
    }


    fun getCisInfo(): Map<String, Any?>? {
        val node = getCisInfoNode() ?: return null
        return mapOf(
            "productGroup" to (node.path("productGroup").asText().takeIf { it.isNotEmpty() }),
            "productGroupId" to (node.path("productGroupId").asInt().takeIf { it != 0 }),
            "ownerName" to (node.path("ownerName").asText().takeIf { it.isNotEmpty() }),
            "ownerInn" to (node.path("ownerInn").asText().takeIf { it.isNotEmpty() }),
            "status" to (node.path("status").asText().takeIf { it.isNotEmpty() }),
            "productionDate" to (node.path("productionDate").asText().takeIf { it.isNotEmpty() }),
            "child" to getChildCis()
        )
    }

    fun JsonNode.asIntOrNull(): Int? {
        return if (this.isInt) this.asInt() else null
    }
}