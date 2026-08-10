package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.repository.agnphonenumberlist.AgnphonenumberlistRepository
import org.springframework.stereotype.Service

@Service
class AgnPhoneService(
    private val agnPhoneFind: AgnPhoneFind,
    private val agnPhoneExists: AgnPhoneExists
) {
    fun searchByPhone(phone: String): List<AgnphonenumberlistDto> {
        return agnPhoneFind.searchByPhone(phone)
    }

    fun existsByPhone(phone: String): Boolean {
        return agnPhoneExists.existsByPhone(phone)
    }
}