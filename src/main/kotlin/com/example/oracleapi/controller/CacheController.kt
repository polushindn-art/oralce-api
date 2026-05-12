package com.example.oracleapi.controller

import org.springframework.cache.CacheManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/cache")
class CacheController(
    private val cacheManager: CacheManager
) {

    @GetMapping("/stats")
    fun getCacheStats(): ResponseEntity<Map<String, Any?>> {
        val cache = cacheManager.getCache("markCache")
        return ResponseEntity.ok(
            mapOf(
                "cacheName" to "markCache",
                "cacheExists" to (cache != null),
                "nativeCache" to cache?.nativeCache?.toString()
            )
        )
    }

    @GetMapping("/contains/{km}")
    fun containsKey(@PathVariable km: String): ResponseEntity<Map<String, Any?>> {
        val cache = cacheManager.getCache("markCache")
        val value = cache?.get(km)?.get()

        return ResponseEntity.ok(
            mapOf(
                "km" to km,
                "exists" to (value != null),
                "value" to value
            )
        )
    }

    @DeleteMapping("/clear")
    fun clearCache(): ResponseEntity<Map<String, String>> {
        val cache = cacheManager.getCache("markCache")
        cache?.clear()
        return ResponseEntity.ok(
            mapOf(
                "message" to "Cache cleared",
                "status" to "success"
            )
        )
    }

    @DeleteMapping("/evict/{km}")
    fun evictKey(@PathVariable km: String): ResponseEntity<Map<String, String>> {
        val cache = cacheManager.getCache("markCache")
        cache?.evict(km)
        return ResponseEntity.ok(
            mapOf(
                "message" to "Key $km evicted from cache",
                "status" to "success"
            )
        )
    }
}