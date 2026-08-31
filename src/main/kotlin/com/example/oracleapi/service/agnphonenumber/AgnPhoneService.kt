package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneCardDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneListAgnDto
import com.example.oracleapi.util.PhoneUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AgnPhoneService(
    private val agnPhoneFind: AgnPhoneFind,
    private val agnPhoneExists: AgnPhoneExists
) {
    @Transactional(readOnly = true)
    fun searchByPhone(phone: String): List<AgnphonenumberlistDto> {
        return agnPhoneFind.searchByPhone(phone)
    }

    @Transactional(readOnly = true)
    fun existsByPhone(phone: String): Boolean {
        return agnPhoneExists.existsByPhone(PhoneUtils.getPhoneTail(phone))
    }

    @Transactional(readOnly = true)
    fun searchCardByPhone(phone: String): List<PhoneCardDto> {
        return agnPhoneFind.searchCardByPhone(phone)
    }

    @Transactional(readOnly = true)
    fun searchPhoneByAgn(rn: Long): List<PhoneListAgnDto> {
        return agnPhoneFind.searchPhoneByAgn(rn)
    }

}