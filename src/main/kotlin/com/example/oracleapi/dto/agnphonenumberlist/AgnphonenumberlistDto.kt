package com.example.oracleapi.dto.agnphonenumberlist

import com.example.oracleapi.entity.table.Agnphonenumberlist
import jakarta.validation.constraints.*
import java.io.Serializable

/**
 * DTO for {@link com.example.oracleapi.entity.table.Agnphonenumberlist}
 */
data class AgnphonenumberlistDto(
    val rn: Long? = null,

    @field:NotNull
    var prnagn: Long? = null,

    val agnname: String? = null,

    val agncode: String? = null,

    val email: String? = null,

    @field:NotNull @field:Size(max = 20)
    var phonenumber: String? = null,

    @field:Size(max = 100)
    val note: String? = null,

    @field:Size(max = 20)
    val phoneTail: String? = null,

    val phoneMasterRN: Long? = null,

) : Serializable {
    companion object {
        fun fromEntity(entity: Agnphonenumberlist): AgnphonenumberlistDto {
            return AgnphonenumberlistDto(
                rn = entity.rn,
                prnagn = entity.prnagn,
                agnname = entity.prnagnEntity?.agnname,
                agncode = entity.prnagnEntity?.agncode,
                email = entity.prnagnEntity?.mail,
                phonenumber = entity.phonenumber,
                note = entity.note,
                phoneTail = entity.phoneTail,
                phoneMasterRN = entity.prnagnEntity?.phonenumberrn
            )
        }
    }
}