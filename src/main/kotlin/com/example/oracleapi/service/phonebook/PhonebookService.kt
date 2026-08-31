package com.example.oracleapi.service.phonebook

import com.example.oracleapi.dto.phonebook.PhonebookDto
import com.example.oracleapi.entity.table.toDto
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhonebookService(
    private val phonebookRepository: PhonebookRepository
) {

    @Transactional(readOnly = true)
    fun getAllRecords(): List<PhonebookDto> {
        return phonebookRepository.findAll().map { it.toDto() }
    }
}