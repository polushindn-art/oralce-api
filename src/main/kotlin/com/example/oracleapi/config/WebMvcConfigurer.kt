package com.example.oracleapi.config

import com.example.oracleapi.interceptor.AppContextInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val appContextInterceptor: AppContextInterceptor
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "https://ya.ars",           // Nginx прокси
                "http://localhost:5173",    // Локальная разработка React (Vite)
                "https://sdl-arsenal.ru",   // Если сайт там
                "https://ya.ars:443"       // Если React на ya.ars
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(appContextInterceptor)
            .addPathPatterns("/v*/**")
            .order(1)
    }

}