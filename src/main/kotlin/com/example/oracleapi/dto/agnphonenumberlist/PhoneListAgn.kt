package com.example.oracleapi.dto.agnphonenumberlist

import com.example.oracleapi.entity.table.Agnphonenumberlist

data class PhoneListAgn(
    val phonenumber: String?,
    val phoneTail: String?,
    val mainPhone: Boolean?,
    val agnName: String?,
    val agnCode: String?
) {
    companion object {
        fun fromEntity(entity: Agnphonenumberlist?): PhoneListAgn {
            return PhoneListAgn(
                entity?.phonenumber,
                entity?.phoneTail,
                entity?.rn == entity?.prnagnEntity?.phonenumberrn,
                entity?.prnagnEntity?.agnname,
                entity?.prnagnEntity?.agncode
            )
        }
    }
}
