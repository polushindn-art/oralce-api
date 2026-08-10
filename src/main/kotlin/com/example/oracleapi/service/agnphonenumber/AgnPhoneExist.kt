package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.repository.agnphonenumberlist.AgnphonenumberlistRepository
import org.springframework.stereotype.Component

@Component
class AgnPhoneExists(
    private val repository: AgnphonenumberlistRepository
) {
    fun existsByPhone(phoneTail: String): Boolean {
        return repository.existsByPhoneTail(phoneTail)
    }
}