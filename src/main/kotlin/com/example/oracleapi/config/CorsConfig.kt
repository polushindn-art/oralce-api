package com.example.oracleapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*") // Разрешаем любые домены и порты
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Маппинг /markdown/** на папку classpath:/markdown/
        registry.addResourceHandler("/markdown/**")
            .addResourceLocations("classpath:/markdown/")
    }
}