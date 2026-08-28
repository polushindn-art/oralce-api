package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.JoinColumnOrFormula
import org.hibernate.annotations.JoinFormula
import java.time.LocalDateTime


@Entity
@Table(name = "AGNLIST", schema = Helper.SCHEME)
class AgnList(
    @Id
    @Column(name = "RN", nullable = false)
    var rn: Long = 0,

    @Column(name = "CRN", nullable = false)
    var crn: Long = 0,

    @Column(name = "AGNCODE", nullable = false, length = 20)
    var agncode: String = "",

    @Column(name = "AGNNAME", nullable = false, length = 320)
    var agnname: String = "",

    @Column(name = "AGNIDNUMB", length = 20)
    var agnidnumb: String? = null,

    @Column(name = "REASON_CODE", length = 20)
    var reasonCode: String? = null,

    @Column(name = "AGNTYPE", nullable = false)
    var agntype: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(
        column = JoinColumn(
            name = Field.AGNTYPE,
            referencedColumnName = Field.FIELD_VALUE,
            insertable = false,
            updatable = false
        )
    )

    @JoinColumnOrFormula(formula = JoinFormula(value = "'${Field.AGNTYPE}'", referencedColumnName = Field.FIELD_NAME))
    var agntypeEntity: Field? = null,

    @Column(name = "ADDR_FACTPOST", length = 160)
    var addrFactpost: String? = null,

    @Column(name = "ADDR_URPOST", length = 160)
    var addrUrpost: String? = null,

    @Column(name = "FAX", length = 20)
    var fax: String? = null,

    @Column(name = "MAIL", length = 40)
    var mail: String? = null,

    @Column(name = "BANKACCNAME", length = 80)
    var bankaccname: String? = null,

    @Column(name = "BANKACCNUMB", length = 40)
    var bankaccnumb: String? = null,

    @Column(name = "BANKACCBIK", length = 40)
    var bankaccbik: String? = null,

    @Column(name = "BANKACCKOR", length = 40)
    var bankacckor: String? = null,

    @Column(name = "PASSPORT_SER", length = 20)
    var passportSer: String? = null,

    @Column(name = "PASSPORT_NUMB", length = 20)
    var passportNumb: String? = null,

    @Column(name = "PASSPORT_WHEN")
    var passportWhen: LocalDateTime? = null,

    @Column(name = "PASSPORT_WHO", length = 80)
    var passportWho: String? = null,

    @Column(name = "AGN_CONTACTS", length = 80)
    var agnContacts: String? = null,

    @Column(name = "NOTE", length = 160)
    var note: String? = null,

    @Column(name = "BUSINESS", length = 80)
    var business: String? = null,

    @Column(name = "LICENCE", length = 80)
    var licence: String? = null,

    @Column(name = "TYPECARD")
    var typecard: Long? = null,

    @Column(name = "AGNGROUP")
    var agngroup: Long? = null,

    @Column(name = "ENABLED", nullable = false)
    var enabled: Long = 1,

    @Column(name = "MANAGER")
    var manager: Long? = null,

    @Column(name = "CASHLESS_DELAY")
    var cashlessDelay: Long? = null,

    @Column(name = "CASHLESS_CREDIT")
    var cashlessCredit: Double? = null,

    @Column(name = "PHONE", length = 80)
    var phone: String? = null,

    @Column(name = "NOMER_DOGOVORA", length = 250)
    var nomerDogovora: String? = null,

    @Column(name = "DATA_DOGOVORA")
    var dataDogovora: LocalDateTime? = null,

    @Column(name = "OKPO", length = 12)
    var okpo: String? = null,

    @Column(name = "DISABLEDAGN")
    var disabledagn: Long = 0,

    @Column(name = "DISABLEDAGNGROUP")
    var disabledagngroup: Long = 0,

    @Column(name = "TYPE_DOG")
    var typeDog: Long? = null,

    @Column(name = "DATA_DOGOVORA_END")
    var dataDogovoraEnd: LocalDateTime? = null,

    @Column(name = "DATA_SALE_END")
    var dataSaleEnd: LocalDateTime? = null,

    @Column(name = "LEADING", nullable = false)
    var leading: Long = 0,

    @Column(name = "SHIPPER")
    var shipper: Long? = null,

    @Column(name = "RECEIVER")
    var receiver: Long? = null,

    @Column(name = "DSCGROUP")
    var dscgroup: Long? = null,

    @Column(name = "DATAAGNOC")
    var dataagnoc: LocalDateTime? = null,

    @Column(name = "REGION")
    var region: Long? = null,

    @Column(name = "CONSIGNEE")
    var consignee: Long? = null,

    @Column(name = "PHONENUMBERRN")
    var phonenumberrn: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PHONENUMBERRN", nullable = false, updatable = false, insertable = false)
    open var phonenumberrnEntity: Agnphonenumberlist? = null,

    @Column(name = "CREATED")
    var created: LocalDateTime? = null,

    @Column(name = "AUTH_REQUIRE")
    var authRequire: Long? = null,

    @Column(name = "TURNOVER")
    var turnover: Double? = null,

    @Column(name = "REALIZATION")
    var realization: Double? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AgnList
        return rn == other.rn
    }

    override fun hashCode(): Int = rn.hashCode()
}