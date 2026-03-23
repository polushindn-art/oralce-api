package com.example.oracleapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val objectMapper: ObjectMapper,
    private val jwtHelper: JwtHelper
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // Настройка CORS
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }

            // Отключаем CSRF для stateless API
            .csrf { csrf -> csrf.disable() }

            // Stateless сессия (не храним сессии на сервере)
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // Настройка авторизации
            .authorizeHttpRequests { auth ->
                auth
                    // Публичные пути
                    .requestMatchers(*JwtHelper.skipPaths).permitAll()

                    // Административные пути
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // Все остальные запросы требуют аутентификации
                    .anyRequest().authenticated()
            }

            // Добавляем JWT фильтр перед стандартным фильтром аутентификации
            .addFilterBefore(
                JwtAuthenticationFilter(jwtHelper, objectMapper),
                UsernamePasswordAuthenticationFilter::class.java
            )

            // Обработка исключений аутентификации
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(CustomAuthenticationEntryPoint())
            }

            // UserDetailsService
            .userDetailsService(userDetailsService)

        return http.build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder()
    }

    /**
     * Настройка CORS для Swagger UI и API-клиентов
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // Разрешаем запросы с этих источников
        configuration.allowedOrigins = listOf(
            "http://localhost:8080",
            "http://localhost:3000",
            "http://127.0.0.1:8080",
            "https://localhost:8080",
            "https://127.0.0.1:8080"
        )

        // Разрешаем все необходимые методы
        configuration.allowedMethods = listOf(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        )

        // Разрешаем заголовки
        configuration.allowedHeaders = listOf(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )

        // Разрешаем отправку cookie
        configuration.allowCredentials = true

        // Заголовки, доступные клиенту
        configuration.exposedHeaders = listOf(
            "Authorization",
            "Set-Cookie"
        )

        // Время жизни preflight запроса (в секундах)
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}