package com.example.oracleapi.service.glossary

import com.example.oracleapi.dto.glossary.GlossaryHistory
import com.example.oracleapi.dto.glossary.GlossaryTerm
import com.example.oracleapi.dto.glossary.SaveGlossaryRequest
import com.example.oracleapi.entity.table.GlossaryHistoryEntity
import com.example.oracleapi.entity.table.GlossaryTermEntity
import com.example.oracleapi.repository.glossary.GlossaryHistoryRepository
import com.example.oracleapi.repository.glossary.GlossaryTermRepository
import com.example.oracleapi.service.public.PublicGenIdRnProcedur
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class GlossaryService(
    private val glossaryTermRepository: GlossaryTermRepository,
    private val glossaryHistoryRepository: GlossaryHistoryRepository,
    private val genIdRnProcedur: PublicGenIdRnProcedur,
    private val jdbcTemplate: JdbcTemplate
) {

    // ============ GET ============

    fun getAllTerms(category: String?, search: String?): List<GlossaryTerm> {
        val entities = when {
            !search.isNullOrBlank() -> glossaryTermRepository.searchActive(search)
            !category.isNullOrBlank() -> glossaryTermRepository.findActiveByCategory(category)
            else -> glossaryTermRepository.findAllActive()
        }
        return entities.map { it.toDto() }
    }

    fun getTerm(rn: Long): GlossaryTerm {
        val entity = glossaryTermRepository.findActiveByRn(rn)
            ?: throw Exception("Термин с RN '$rn' не найден")
        return entity.toDto()
    }

    fun getCategories(): List<String> {
        return glossaryTermRepository.findDistinctCategories()
    }

    fun searchTerms(query: String): List<GlossaryTerm> {
        return glossaryTermRepository.searchActive(query).map { it.toDto() }
    }

    fun getHistory(termRn: Long): List<GlossaryHistory> {
        return glossaryHistoryRepository.findByTermRnOrderByVersionDesc(termRn)
            .map { it.toDto() }
    }

    // ============ CREATE ============

    @Transactional
    fun createTerm(request: SaveGlossaryRequest): GlossaryTerm {
        // 1. Генерация RN
        val genIdResponse = genIdRnProcedur.take()
        val newRn = genIdResponse.rn ?: throw Exception("Не удалось сгенерировать RN")

        // 2. Проверка дубликата
        if (glossaryTermRepository.existsByTermIgnoreCase(request.term)) {
            throw Exception("Термин '${request.term}' уже существует")
        }

        // 3. Создание сущности
        val entity = GlossaryTermEntity(
            rn = newRn,
            term = request.term,
            definition = request.definition,
            category = request.category,
            sortOrder = 0
        )

        // 4. Сохранение
        val saved = glossaryTermRepository.save(entity)
        return saved.toDto()
    }

    // ============ UPDATE ============

    @Transactional
    fun updateTerm(rn: Long, request: SaveGlossaryRequest): GlossaryTerm {
        // 1. Поиск термина
        val entity = glossaryTermRepository.findActiveByRn(rn)
            ?: throw Exception("Термин с RN '$rn' не найден")

        // 2. Проверка дубликата названия
        if (entity.term != request.term &&
            glossaryTermRepository.existsByTermIgnoreCaseAndRnNot(request.term, rn)
        ) {
            throw Exception("Термин '${request.term}' уже существует")
        }

        // 3. Сохранение в историю (текущая версия)
        val historyEntity = GlossaryHistoryEntity.fromTerm(entity)
        glossaryHistoryRepository.save(historyEntity)

        // 4. Обновление
        entity.updateFrom(request)
        val updated = glossaryTermRepository.save(entity)

        return updated.toDto()
    }

    // ============ DELETE ============

    @Transactional
    fun deleteTerm(rn: Long) {
        val entity = glossaryTermRepository.findActiveByRn(rn)
            ?: throw Exception("Термин с RN '$rn' не найден")

        entity.markDeleted()
        glossaryTermRepository.save(entity)
    }

    // ============ HARD DELETE (опционально) ============

    @Transactional
    fun hardDeleteTerm(rn: Long) {
        /*val entity = glossaryTermRepository.findActiveByRn(rn)
            ?: throw Exception("Термин с RN '$rn' не найден")*/
        val entity = glossaryTermRepository.findByRn(rn)
            ?: throw Exception("Термин с RN '$rn' не найден")

        glossaryHistoryRepository.deleteByTermRn(rn)
        glossaryTermRepository.delete(entity)
    }

    // ============ EXPORT ============

    fun exportToMarkdown(): String {
        val terms = getAllTerms(null, null)
        val sb = StringBuilder()

        sb.appendLine("# СЛОВАРЬ ТЕРМИНОВ")
        sb.appendLine()
        sb.appendLine("| Термин | Определение |")
        sb.appendLine("|--------|-------------|")

        var currentCategory: String? = null
        for (term in terms) {
            if (term.category != currentCategory) {
                currentCategory = term.category
                if (currentCategory != null) {
                    sb.appendLine()
                    sb.appendLine("### ${currentCategory}")
                    sb.appendLine()
                }
            }
            sb.appendLine("| **${term.term}** | ${term.definition} |")
        }

        return sb.toString()
    }

    // ============ MAPPERS ============

    private fun GlossaryTermEntity.toDto(): GlossaryTerm {
        return GlossaryTerm(
            rn = this.rn ?: throw Exception("RN не может быть null"),
            term = this.term ?: throw Exception("Term не может быть null"),
            definition = this.definition ?: throw Exception("Definition не может быть null"),
            category = this.category,
            sortOrder = this.sortOrder,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            version = this.version
        )
    }

    private fun GlossaryHistoryEntity.toDto(): GlossaryHistory {
        return GlossaryHistory(
            rn = this.rn ?: throw Exception("RN не может быть null"),
            termRn = this.termRn ?: throw Exception("TermRn не может быть null"),
            term = this.term ?: "",
            definition = this.definition ?: "",
            version = this.version,
            changedBy = this.changedBy,
            changedAt = this.changedAt
        )
    }

    fun getDeletedTerms(): List<GlossaryTerm> {
        val entities = glossaryTermRepository.findAllDeleted()
        return entities.map { it.toDto() }
    }

    @Transactional
    fun restoreTerm(rn: Long): GlossaryTerm {
        val entity = glossaryTermRepository.findDeletedByRn(rn)
            ?: throw Exception("Удалённый термин с RN '$rn' не найден")

        entity.restore()
        val restored = glossaryTermRepository.save(entity)

        // Сохраняем в историю через репозиторий (как в updateTerm)
        val historyEntity = GlossaryHistoryEntity(
            rn = System.currentTimeMillis(),
            termRn = rn,
            term = "ВОССТАНОВЛЕН: " + entity.term,
            definition = entity.definition ?: "",
            version = entity.version + 1,
            changedBy = "system"
        )
        glossaryHistoryRepository.save(historyEntity)

        return restored.toDto()
    }

}