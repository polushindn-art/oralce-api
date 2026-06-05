package com.example.oracleapi.service.mark

import com.example.oracleapi.dto.mark.VMarkFindResponse
import com.example.oracleapi.dto.vMark.MarkSearchResponse
import com.example.oracleapi.repository.VMarkFindRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MarkSearch(
    private val parseMark: ParseMark,
    private val vMarkFindRepository: VMarkFindRepository
) {
    private val logger = LoggerFactory.getLogger(MarkSearch::class.java)
    fun findMark(km: String): MarkSearchResponse {
        logger.debug("Поиск маркировки по коду: $km")
        val parseResult = parseMark.take(km)

        val dbRecord = when {
            !parseResult.cis.isNullOrBlank() -> vMarkFindRepository.findByCis(parseResult.cis)
            !parseResult.sscc.isNullOrBlank() -> vMarkFindRepository.findByCis(parseResult.sscc)
            else -> null
        }

        return MarkSearchResponse(
            found = dbRecord != null,
            parseResult = parseResult,
            mark = dbRecord?.let { VMarkFindResponse.fromEntity(it) }
        )
    }
}