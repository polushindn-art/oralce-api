package com.example.oracleapi

import io.jsonwebtoken.Jwts

class Common {

    companion object {
        const val COOCKIENAME = "accessToken"
        const val SECRETKEY: String = "abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234"
        val skipPaths = arrayOf(
            "/api/health",
            "/auth/token",
            "/login",
            "/swagger-ui/**",      // для всех вложенных путей
            "/v3/api-docs/**",
            "/favicon.ico",
            "/doc.html",
            "/webjars/**",
            "/error",
            "/not-found",
            "/auth/**",             // если нужно открыть все под auth
            "/knife4j/**")
    }

    fun getSubjectFromToken(token: String): String? {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(SECRETKEY.toByteArray())
                .build()
                .parseClaimsJws(token)
                .body
            return claims.subject
        } catch (_: Exception) {
            return null
        }
    }
}