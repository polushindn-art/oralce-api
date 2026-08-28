package com.example.oracleapi.service.agnphonenumber

import com.example.oracleapi.dto.agnphonenumberlist.AgnphonenumberlistDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneCardDto
import com.example.oracleapi.dto.agnphonenumberlist.PhoneListAgn
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
        val searchTail = PhoneUtils.getPhoneTail(rawPhone)
        val entities = repository.findByPhoneTail(searchTail)
        return entities.map { AgnphonenumberlistDto.fromEntity(it) }
    }

    fun searchCardByPhone(rawPhone: String): List<PhoneCardDto> {
        val searchTail = PhoneUtils.getPhoneTail(rawPhone)
        val result = repository.findPhoneAndCardByPhoneTail(searchTail)
        return result.map {
            PhoneCardDto(
                phonenumber = it.getPhonenumber(),
                dscbarnumb = it.getDscbarnumb(),
                agnname = it.getAgnname()
            )
        }
    }

    fun searchPhoneByAgn(rn: Long): List<PhoneListAgn> {
        val result = repository.findAllByPrnagn(rn)
        return result.map { PhoneListAgn.fromEntity(it) }
    }

}