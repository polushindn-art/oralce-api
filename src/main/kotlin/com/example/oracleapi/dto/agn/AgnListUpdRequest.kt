package com.example.oracleapi.dto.agn

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

data class AgnListUpdRequest(
    @field:NotNull(message = "RN не может быть null")
    @field:Positive(message = "RN должен быть больше 0")
    val rn: Long,
    val agncode: String? = null,
    val agnname: String? = null,
    val agnidnumb: String? = null,
    val reasonCode: String? = null,
    val agntype: Long? = null,
    val addrFactpost: String? = null,
    val addrUrpost: String? = null,
    val phone: String? = null,
    val phonenumberrn: Long? = null,
    val fax: String? = null,
    val mail: String? = null,
    val bankaccname: String? = null,
    val bankaccnumb: String? = null,
    val bankaccbik: String? = null,
    val bankacckor: String? = null,
    val passportSer: String? = null,
    val passportNumb: String? = null,
    val passportWhen: LocalDate? = null,
    val passportWho: String? = null,
    val agnContacts: String? = null,
    val agngroup: Long? = null,
    val manager: Long? = null,
    val typecard: Long? = null,
    val business: String? = null,
    val licence: String? = null,
    val enabled: Long? = 1,
    val cashlessDelay: Long? = null,
    val cashlessCredit: BigDecimal? = null,
    val note: String? = null,
    val nomerDogovora: String? = null,
    val dataDogovora: LocalDate? = null,
    val okpo: String? = null,
    val disabledagn: Long? = 0,
    val disabledagngroup: Long? = 0,
    val typeDog: Long? = null,
    val dataDogovoraEnd: LocalDate? = null,
    val dataSaleEnd: LocalDate? = null,
    val supplier: Long? = null,
    val leading: Long? = 0,
    val shipper: Long? = null,
    val receiver: Long? = null,
    val dscgroup: Long? = null,
    val region: Long? = null,
    val consignee: Long? = null,
    val authRequire: Long? = null
)

data class AgnListUpdResponse(
    val rn: Long,
    val message: String = "Контрагент успешно обновлён"
)