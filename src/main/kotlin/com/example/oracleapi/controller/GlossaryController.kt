package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.glossary.ExportMarkdownResponse
import com.example.oracleapi.dto.glossary.GlossaryHistoryResponse
import com.example.oracleapi.dto.glossary.GlossaryTermResponse
import com.example.oracleapi.dto.glossary.SaveGlossaryRequest
import com.example.oracleapi.service.glossary.GlossaryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/v1/glossary")
@Tag(name = "Словарь терминов", description = "API для работы со словарём терминов")
class GlossaryController(
    private val glossaryService: GlossaryService
) : BaseController() {

    // ============================================================
    // GET
    // ============================================================

    @GetMapping("/terms")
    @Operation(summary = "Получить список всех терминов")
    fun getAllTerms(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) search: String?
    ): MyApiResponse<List<GlossaryTermResponse>> {
        return successList(glossaryService.getAllTerms(category, search))
    }

    @GetMapping("/term/{rn}")
    @Operation(summary = "Получить термин по RN")
    fun getTerm(@PathVariable rn: Long): MyApiResponse<GlossaryTermResponse> {
        return success(glossaryService.getTerm(rn))
    }

    @GetMapping("/categories")
    @Operation(summary = "Получить список всех категорий")
    fun getCategories(): MyApiResponse<List<String>> {
        return successList(glossaryService.getCategories())
    }

    @GetMapping("/history/{rn}")
    @Operation(summary = "Получить историю изменений термина")
    fun getHistory(@PathVariable rn: Long): MyApiResponse<List<GlossaryHistoryResponse>> {
        return successList(glossaryService.getHistory(rn))
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск терминов")
    fun searchTerms(@RequestParam query: String): MyApiResponse<List<GlossaryTermResponse>> {
        return successList(glossaryService.searchTerms(query))
    }

    @GetMapping("/export/markdown")
    @Operation(summary = "Экспортировать словарь в Markdown")
    fun exportMarkdown(): MyApiResponse<ExportMarkdownResponse> {
        val content = glossaryService.exportToMarkdown()
        val terms = glossaryService.getAllTerms(null, null)

        return success(
            ExportMarkdownResponse(
                content = content,
                fileName = "словарь_терминов_${LocalDate.now()}.md",
                totalTerms = terms.size
            ),
            message = "Экспорт выполнен, ${terms.size} терминов"
        )
    }

    // ============================================================
    // POST
    // ============================================================

    @PostMapping("/term")
    @Operation(summary = "Создать новый термин")
    fun createTerm(@RequestBody request: SaveGlossaryRequest): MyApiResponse<GlossaryTermResponse> {
        return try {
            success(glossaryService.createTerm(request), "Термин создан")
        } catch (e: Exception) {
            error(e.message ?: "Ошибка создания")
        }
    }

    @PostMapping("/terms/bulk")
    @Operation(summary = "Массовое создание терминов")
    fun createTermsBulk(@RequestBody terms: List<SaveGlossaryRequest>): MyApiResponse<Map<String, Any>> {
        val results = terms.map { request ->
            try {
                glossaryService.createTerm(request)
                mapOf(
                    "term" to request.term,
                    "success" to true,
                    "message" to "OK"
                )
            } catch (e: Exception) {
                mapOf(
                    "term" to request.term,
                    "success" to false,
                    "message" to (e.message ?: "Ошибка")
                )
            }
        }

        val successCount = results.count { it["success"] == true }
        val failCount = results.count { it["success"] == false }

        return success(
            mapOf(
                "total" to terms.size,
                "success_count" to successCount,
                "fail_count" to failCount,
                "results" to results
            ),
            if (failCount == 0) "Все термины созданы" else "Создано $successCount из ${terms.size}"
        )
    }

    // ============================================================
    // PUT
    // ============================================================

    @PutMapping("/term/{rn}")
    @Operation(summary = "Обновить термин")
    fun updateTerm(
        @PathVariable rn: Long,
        @RequestBody request: SaveGlossaryRequest
    ): MyApiResponse<GlossaryTermResponse> {
        return try {
            success(glossaryService.updateTerm(rn, request), "Термин обновлён")
        } catch (e: Exception) {
            error(e.message ?: "Ошибка обновления")
        }
    }

    // ============================================================
    // DELETE
    // ============================================================

    @DeleteMapping("/term/{rn}")
    @Operation(summary = "Удалить термин (мягкое удаление)")
    fun deleteTerm(@PathVariable rn: Long): MyApiResponse<Unit> {
        return try {
            glossaryService.deleteTerm(rn)
            success("Термин удалён")
        } catch (e: Exception) {
            error(e.message ?: "Ошибка удаления")
        }
    }

    @DeleteMapping("/term/{rn}/hard")
    @Operation(summary = "Полностью удалить термин (без возможности восстановления)")
    fun hardDeleteTerm(@PathVariable rn: Long): MyApiResponse<Unit> {
        return try {
            glossaryService.hardDeleteTerm(rn)
            success("Термин полностью удалён")
        } catch (e: Exception) {
            error(e.message ?: "Ошибка удаления")
        }
    }

    @GetMapping("/terms/deleted")
    @Operation(summary = "Получить список удалённых терминов")
    fun getDeletedTerms(): MyApiResponse<List<GlossaryTermResponse>> {
        return successList(glossaryService.getDeletedTerms())
    }

    @PutMapping("/term/{rn}/restore")
    @Operation(summary = "Восстановить удалённый термин")
    fun restoreTerm(@PathVariable rn: Long): MyApiResponse<GlossaryTermResponse> {
        return try {
            val term = glossaryService.restoreTerm(rn)
            success(term, "Термин восстановлен")
        } catch (e: Exception) {
            error(e.message ?: "Ошибка восстановления")
        }
    }

}