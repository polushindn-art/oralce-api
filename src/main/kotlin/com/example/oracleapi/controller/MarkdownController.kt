package com.example.oracleapi.controller

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

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
            // Получаем имя файла с правильной кодировкой
            val filename = resource.filename ?: return@forEach
            val fullPath = resource.url.toString().substringAfter("/markdown/")
            val parts = fullPath.split("/")

            when (parts.size) {
                1 -> {
                    val name = filename.replace(".md", "")
                    result.getOrPut("root") { mutableListOf() }.add(
                        FileNode(name, filename, "file")
                    )
                }
                else -> {
                    val folder = parts[0]
                    val name = filename.replace(".md", "")
                    result.getOrPut(folder) { mutableListOf() }.add(
                        FileNode(name, fullPath, "file")
                    )
                }
            }
        }

        return result.toSortedMap().mapValues { (_, files) ->
            files.sortedBy { it.name }
        }
    }
}