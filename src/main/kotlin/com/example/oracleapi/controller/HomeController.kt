package com.example.oracleapi.controller

import com.example.oracleapi.service.HostService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController(
    private val hostService: HostService
) {
    @GetMapping("/")
    fun hom(model: Model): String {
        model.addAttribute("host", hostService.getHost())
        return "index"
    }
}