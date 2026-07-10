package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.website.WebSiteRequest
import com.example.oracleapi.dto.website.WebSiteResponse
import com.example.oracleapi.service.website.WebSiteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/website")
@Tag(name = "Сайт", description = "Сайт")
class SiteController(
    private val webSiteService: WebSiteService
) : BaseController() {

    @PostMapping("/get_link")
    @Operation(summary = "Получить ссылку для номенклатуры")
    fun getLink(
        @Valid @RequestBody rtequest: WebSiteRequest
    ): MyApiResponse<WebSiteResponse> {
        return success(webSiteService.getLinkWebSite(rtequest))
    }
}

