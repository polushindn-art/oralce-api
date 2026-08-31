package com.example.oracleapi.dto.agnphonenumberlist

import com.example.oracleapi.entity.table.Agnphonenumberlist

data class PhoneListAgnDto(
    val phonenumber: String?,
    val phoneTail: String?,
    val mainPhone: Boolean?,
    val agnName: String?,
    val agnCode: String?
) {
    companion object {
        fun fromEntity(entity: Agnphonenumberlist?): PhoneListAgnDto {
            return PhoneListAgnDto(
                entity?.phonenumber,
                entity?.phoneTail,
                entity?.rn == entity?.prnagnEntity?.phonenumberrn,
                entity?.prnagnEntity?.agnname,
                entity?.prnagnEntity?.agncode
            )
        }
    }
}
