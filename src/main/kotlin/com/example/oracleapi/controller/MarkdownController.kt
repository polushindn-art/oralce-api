package com.example.oracleapi.controller

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/markdown")
class MarkdownController {

    data class FileNode(
        val name: String,
        val path: String,
        val type: String
    )

    @GetMapping("/files-tree", produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getFilesTree(): Map<String, List<FileNode>> {
        val resolver = PathMatchingResourcePatternResolver()
        val resources = resolver.getResources("classpath:/markdown/**/*.md")

        val result = mutableMapOf<String, MutableList<FileNode>>()

        resources.forEach { resource ->
            val fullPath = resource.url.toString().substringAfter("/markdown/")
            val decodedPath = URLDecoder.decode(fullPath, StandardCharsets.UTF_8.toString())
            val parts = decodedPath.split("/")

            val filename = resource.filename ?: return@forEach
            val name = filename.replace(".md", "")

            when (parts.size) {
                1 -> {
                    result.getOrPut("root") { mutableListOf() }.add(
                        FileNode(name, filename, "file")
                    )
                }
                else -> {
                    val folder = parts[0]
                    result.getOrPut(folder) { mutableListOf() }.add(
                        FileNode(name, decodedPath, "file")
                    )
                }
            }
        }

        return result.toSortedMap().mapValues { (_, files) ->
            files.sortedBy { it.name }
        }
    }
}