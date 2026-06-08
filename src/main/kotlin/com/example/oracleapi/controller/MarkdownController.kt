package com.example.oracleapi.controller

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/markdown")
class MarkdownController {

    data class FileNode(
        val name: String,
        val path: String,
        val type: String
    )

    @GetMapping("/files-tree")
    fun getFilesTree(): Map<String, List<FileNode>> {
        val resolver = PathMatchingResourcePatternResolver()
        val resources = resolver.getResources("classpath:/markdown/**/*.md")

        val result = mutableMapOf<String, MutableList<FileNode>>()

        resources.forEach { resource ->
            val fullPath = resource.url.toString().substringAfter("/markdown/")
            val parts = fullPath.split("/")

            when (parts.size) {
                1 -> {
                    // Файл в корне
                    result.getOrPut("root") { mutableListOf() }.add(
                        FileNode(parts[0].replace(".md", ""), parts[0], "file")
                    )
                }
                else -> {
                    // Файл в папке
                    val folder = parts[0]
                    val filename = parts.last().replace(".md", "")
                    result.getOrPut(folder) { mutableListOf() }.add(
                        FileNode(filename, fullPath, "file")
                    )
                }
            }
        }

        // Сортируем папки и файлы
        return result.toSortedMap().mapValues { (_, files) ->
            files.sortedBy { it.name }
        }
    }
}