package com.example.oracleapi.dto.agnlist

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate

data class AgnListInsRequest(
    // ========== ОБЯЗАТЕЛЬНЫЕ ПОЛЯ ==========
    @field:NotNull(message = "CRN не может быть null")
    @field:Positive(message = "CRN должен быть больше 0")
    val crn: Long,

    @field:NotBlank(message = "AGNCODE не может быть пустым")
    @field:Size(max = 20, message = "AGNCODE не более 20 символов")
    val agncode: String,

    @field:NotBlank(message = "AGNNAME не может быть пустым")
    @field:Size(max = 320, message = "AGNNAME не более 320 символов")
    val agnname: String,

    @field:NotNull(message = "AGNTYPE не может быть null")
    val agntype: Long,

    @field:NotNull(message = "ENABLED не может быть null")
    val enabled: Long = 1,

    @field:NotNull(message = "LEADING не может быть null")
    val leading: Long = 0,

    // ========== ОПЦИОНАЛЬНЫЕ ПОЛЯ ==========
    @field:Size(max = 20, message = "AGNIDNUMB не более 20 символов")
    val agnidnumb: String? = null,

    @field:Size(max = 20, message = "REASON_CODE не более 20 символов")
    val reasonCode: String? = null,

    @field:Size(max = 160, message = "ADDR_FACTPOST не более 160 символов")
    val addrFactpost: String? = null,

    @field:Size(max = 160, message = "ADDR_URPOST не более 160 символов")
    val addrUrpost: String? = null,

    @field:Size(max = 20, message = "FAX не более 20 символов")
    val fax: String? = null,

    @field:Size(max = 40, message = "MAIL не более 40 символов")
    @field:Email(message = "Неверный формат email")
    val mail: String? = null,

    @field:Size(max = 80, message = "BANKACCNAME не более 80 символов")
    val bankaccname: String? = null,

    @field:Size(max = 40, message = "BANKACCNUMB не более 40 символов")
    val bankaccnumb: String? = null,

    @field:Size(max = 40, message = "BANKACCBIK не более 40 символов")
    val bankaccbik: String? = null,

    @field:Size(max = 40, message = "BANKACCKOR не более 40 символов")
    val bankacckor: String? = null,

    @field:Size(max = 20, message = "PASSPORT_SER не более 20 символов")
    val passportSer: String? = null,

    @field:Size(max = 20, message = "PASSPORT_NUMB не более 20 символов")
    val passportNumb: String? = null,

    val passportWhen: LocalDate? = null,

    @field:Size(max = 80, message = "PASSPORT_WHO не более 80 символов")
    val passportWho: String? = null,

    @field:Size(max = 80, message = "AGN_CONTACTS не более 80 символов")
    val agnContacts: String? = null,

    @field:Size(max = 160, message = "NOTE не более 160 символов")
    val note: String? = null,

    @field:Size(max = 80, message = "BUSINESS не более 80 символов")
    val business: String? = null,

    @field:Size(max = 80, message = "LICENCE не более 80 символов")
    val licence: String? = null,

    val typecard: Long? = null,
    val agngroup: Long? = null,
    val manager: Long? = null,
    val cashlessDelay: Long? = null,
    val cashlessCredit: BigDecimal? = null,

    @field:Size(max = 80, message = "PHONE не более 80 символов")
    val phone: String? = null,

    @field:Size(max = 250, message = "NOMER_DOGOVORA не более 250 символов")
    val nomerDogovora: String? = null,

    val dataDogovora: LocalDate? = null,

    @field:Size(max = 12, message = "OKPO не более 12 символов")
    val okpo: String? = null,

    val disabledagn: Long? = 0,
    val disabledagngroup: Long? = 0,
    val typeDog: Long? = null,
    val dataDogovoraEnd: LocalDate? = null,
    val dataSaleEnd: LocalDate? = null,
    val supplier: Long? = null,
    val shipper: Long? = null,
    val receiver: Long? = null,
    val dscgroup: Long? = null,
    val region: Long? = null,
    val consignee: Long? = null,
    val authRequire: Long? = null
)
