package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneCardDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneListAgn
import com.example.oracleapi.util.PhoneUtils
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
        return agnPhoneExists.existsByPhone(PhoneUtils.getPhoneTail(phone))
    }

    fun searchCardByPhone(phone: String): List<PhoneCardDto> {
        return agnPhoneFind.searchCardByPhone(phone)
    }

    fun searchPhoneByAgn(rn: Long): List<PhoneListAgn> {
        return agnPhoneFind.searchPhoneByAgn(rn)
    }

}