package com.example.oracleapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdvancedSwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API АРСЕНАЛ")
                    .description("Выполните /auth/token для выполения запросов api")
                    .version("1.0.0")
            )
            .components(
                Components()
                    .addSecuritySchemes("Укажите токен авторизации",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .description("Если нет токена тогда достаточно выполнить /auth/token (тогда значение токена указывать не обязательно)")
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
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

    @Bean
    fun qrealOpenApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("rest-api")
            .pathsToMatch("/**")
            .build()
    }

    //@Bean
    fun publicGroupedOpenApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public-api")
            .pathsToMatch("/api/**")
            .build()
    }

}