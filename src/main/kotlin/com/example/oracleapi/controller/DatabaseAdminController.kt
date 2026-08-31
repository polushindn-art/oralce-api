package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.service.admin.DatabaseAdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/admin/db")
@Tag(name = "Администрирование БД")
class DatabaseAdminController(
    private val databaseAdminService: DatabaseAdminService
) : BaseController() {

    @PostMapping("/evict-pool")
    @Operation(summary = "Мягкий сброс пула соединений HikariCP")
    fun evictPool(): MyApiResponse<Unit> {
        databaseAdminService.softEvictPoolConnections()
        return success("Пул соединений HikariCP успешно обновлен (soft evict выполнен).")
    }
}