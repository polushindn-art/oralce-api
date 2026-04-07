package com.example.oracleapi.controller

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

// DTO для ответа
data class DbInfoResponse(
    val url: String,
    val host: String,
    val port: String,
    val databaseName: String,
    val user: String,
    val databaseProductName: String,
    val databaseProductVersion: String,
    val driverName: String,
    val isProd: Boolean,
    val activeProfile: String
)

data class ProfileResponse(
    val activeProfiles: String,
    val isDev: Boolean,
    val isProd: Boolean
)

@RestController
@RequestMapping("/info")
@Tag(name = "Информация", description = "Информация о приложении")
class InfoController(
    private val environment: Environment,
    private val dataSource: DataSource
) {

    @GetMapping("/profile")
    @Operation(summary = "Текущий профиль", description = "Возвращает активный профиль Spring")
    fun getProfile(): ApiResponse<ProfileResponse> {
        val profiles = environment.activeProfiles.joinToString(", ")
        return ApiResponse.success(
            data = ProfileResponse(
                activeProfiles = profiles,
                isDev = environment.activeProfiles.contains("dev"),
                isProd = environment.activeProfiles.contains("prod")
            ),
            message = "Текущий профиль: $profiles"
        )
    }

    @GetMapping("/db-info")
    @Operation(summary = "Информация о БД", description = "Возвращает информацию о подключении к БД")
    fun getDbInfo(): ApiResponse<DbInfoResponse> {
        return try {
            val connection = dataSource.connection
            val jdbcUrl = connection.metaData.url ?: "unknown"
            val (host, port, dbName) = Helper.parseOracleJdbcUrl(jdbcUrl)
            val dbInfo = DbInfoResponse(
                url = connection.metaData.url ?: "unknown",
                host = host,
                port = port,
                databaseName = dbName,
                user = connection.metaData.userName ?: "unknown",
                databaseProductName = connection.metaData.databaseProductName ?: "unknown",
                databaseProductVersion = connection.metaData.databaseProductVersion ?: "unknown",
                driverName = connection.metaData.driverName ?: "unknown",
                isProd = environment.activeProfiles.contains("prod"),
                activeProfile = environment.activeProfiles.firstOrNull() ?: "unknown"
            )
            connection.close()
            ApiResponse.success(
                data = dbInfo,
                message = "Информация о БД получена"
            )
        } catch (e: Exception) {
            ApiResponse.error(
                message = "Ошибка получения информации о БД: ${e.message}"
            )
        }
    }

}