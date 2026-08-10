package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.repository.agnphonenumberlist.AgnphonenumberlistRepository
import com.example.oracleapi.util.PhoneUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AgnPhoneFind(
    private val repository: AgnphonenumberlistRepository
) {
    @Transactional(readOnly = true)
    fun searchByPhone(rawPhone: String): List<AgnphonenumberlistDto> {
        val searchTail = PhoneUtils().getPhoneTail(rawPhone)
        val entities = repository.findByPhoneTail(searchTail)
        return entities.map { AgnphonenumberlistDto.fromEntity(it) }
    }

}