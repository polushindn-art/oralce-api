package com.example.oracleapi.dto.phonebook

import com.example.oracleapi.annotation.BindingDateFormat
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDate

/**
 * DTO for {@link com.example.oracleapi.entity.table.Phonebook}
 */
data class PhonebookDto(
    @field:NotNull val rn: Long? = null,
    @field:Size(max = 30) val nname: String? = null,
    @field:Size(max = 30) val fname: String? = null,
    @field:Size(max = 30) val lname: String? = null,
    @field:Size(max = 50) val email: String? = null,
    @field:Size(max = 20) val pbe: String? = null,
    @field:BindingDateFormat
    val rdate: LocalDate? = null,
    @field:Size(max = 10) val phoneInt: String? = null,
    @field:Size(max = 25) val phoneSot: String? = null,
    @field:Size(max = 50) val dolgnost: String? = null,
    @field:Size(max = 100) val otdel: String? = null,
    val phoneTail: String? = null
) : Serializable