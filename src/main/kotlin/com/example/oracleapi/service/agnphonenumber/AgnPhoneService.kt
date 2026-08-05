package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import org.springframework.stereotype.Service

@Service
class AgnPhoneService(
    private val agnPhoneFind: AgnPhoneFind
) {
    fun searchByPhone(phone: String): List<AgnphonenumberlistDto> {
        return agnPhoneFind.searchByPhone(phone)
    }
}