package com.example.oracleapi.service.agnList

import com.example.oracleapi.dto.agn.AgnListInfoResponse
import com.example.oracleapi.dto.agnphonenumberlist.PhoneListAgn
import com.example.oracleapi.repository.agnlist.AgnListRepository
import com.example.oracleapi.repository.agnphonenumberlist.AgnphonenumberlistRepository
import org.springframework.stereotype.Component

@Component
class AgnListInfo(
    private val agnlistRepository: AgnListRepository,
    private val agnphonenumberlistRepository: AgnphonenumberlistRepository
) {
    fun getByRnInfo(rn: Long): AgnListInfoResponse {
        val agnList = agnlistRepository.findByRn(rn) ?: throw IllegalArgumentException("Контрагент с RN=$rn не найден")
        val phoneEntities = agnphonenumberlistRepository.findAllByPrnagn(rn)

        val phoneDtoList = phoneEntities.map { phone ->
            PhoneListAgn(
                phone.phonenumber,
                phone.phoneTail,
                phone.rn == phone.prnagnEntity?.phonenumberrn,
                phone.prnagnEntity?.agnname,
                phone.prnagnEntity?.agncode
            )
        }

        return AgnListInfoResponse(
            rn = agnList.rn,
            agnName = agnList.agnname,
            agnCode = agnList.agncode,
            phone = phoneDtoList.takeIf { it.isNotEmpty() },
            inn = agnList.agnidnumb,
            kpp = agnList.reasonCode,
            agnType = agnList.agntype,
            agnTypeName = agnList.agntypeEntity?.fieldComment,
            addressFact = agnList.addrFactpost,
            addressUl = agnList.addrUrpost,
            eMail = agnList.mail
        )

    }
}