package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@Entity
@Table(name = "PHONEBOOK", schema = Helper.SCHEME)
open class Phonebook {
    @Id
    @NotNull
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 30)
    @Column(name = "NNAME", length = 30)
    open var nname: String? = null

    @Size(max = 30)
    @Column(name = "FNAME", length = 30)
    open var fname: String? = null

    @Size(max = 30)
    @Column(name = "LNAME", length = 30)
    open var lname: String? = null

    @Size(max = 50)
    @Column(name = "EMAIL", length = 50)
    open var email: String? = null

    @Size(max = 20)
    @Column(name = "PBE", length = 20)
    open var pbe: String? = null

    @Column(name = "RDATE")
    open var rdate: LocalDate? = null

    @Size(max = 10)
    @Column(name = "PHONE_INT", length = 10)
    open var phoneInt: String? = null

    @Size(max = 25)
    @Column(name = "PHONE_SOT", length = 25)
    open var phoneSot: String? = null

    @Size(max = 50)
    @Column(name = "DOLGNOST", length = 50)
    open var dolgnost: String? = null

    @Size(max = 100)
    @Column(name = "OTDEL", length = 100)
    open var otdel: String? = null

    @Column(name = "KARMA")
    open var karma: Long? = null

}