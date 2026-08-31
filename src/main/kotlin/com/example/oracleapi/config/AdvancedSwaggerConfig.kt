package com.example.oracleapi.config

import com.example.oracleapi.Helper
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdvancedSwaggerConfig(
    @param:Value("\${spring.datasource.url:}")
    private val datasourceUrl: String
) {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val host = Helper.parseOracleJdbcUrl(datasourceUrl)
        return OpenAPI()
            .info(
                Info()
                    .title("🚀 API АРСЕНАЛ - $host")
                    .description("Выполните /auth/token для выполнения запросов api")
                    .version("1.0.0")
            )
            .security(
                listOf(SecurityRequirement().addList("bearer-key"))
            )
    }

    @Bean
    fun groupedOpenApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("main-api")
            .pathsToMatch("/**")
            .build()
    }
}