package com.example.oracleapi.service.website

import com.example.oracleapi.dto.website.WebSiteRequest
import com.example.oracleapi.dto.website.WebSiteResponse
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class WebSiteService {

    fun getLinkWebSite(request: WebSiteRequest): WebSiteResponse {
        val article = request.article
        val withoutB = article.takeLast(article.length - 1).take(10)
        return WebSiteResponse(
            "sdl-arsenal.ru/catalog/?q=${withoutB}"
        )
    }
}