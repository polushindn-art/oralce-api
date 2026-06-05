package com.example.oracleapi.controller

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/markdown")
class MarkdownController {

    @GetMapping("/files")
    fun getMarkdownFiles(): List<String> {
        val resolver = PathMatchingResourcePatternResolver()
        val resources = resolver.getResources("classpath:/markdown/*.md")

        return resources.mapNotNull { it.filename }.sorted()
    }
}