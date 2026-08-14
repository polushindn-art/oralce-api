package com.example.oracleapi.controller

import com.example.oracleapi.dto.common.MyApiResponse
import com.example.oracleapi.dto.phonebook.PhonebookDto
import com.example.oracleapi.service.phonebook.PhonebookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/phonebook")
@Tag(name = "Телефонный справочник", description = "Содержит информацию о телефонах")
class PhoneBookController(
    private val phonebookService: PhonebookService
):BaseController() {
    @GetMapping("/all")
    @Operation(
        summary = "Список",
        description = "Возвращает весь список"
    )
    fun getAllRecords(): MyApiResponse<List<PhonebookDto>> {
        return successList(phonebookService.getAllRecords())
    }
}