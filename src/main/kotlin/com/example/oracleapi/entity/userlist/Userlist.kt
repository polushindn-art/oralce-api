package com.example.oracleapi.entity.userlist

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "USERLIST", schema = Helper.SCHEME)
open class Userlist {
    @Id
    @Column(name = "RN", columnDefinition = "unknown")
    open var rn: Long? = null

    @Column(name = "USERCODE", columnDefinition = "unknown")
    open var usercode: String? = null

    @Column(name = "USERAGN", columnDefinition = "unknown")
    open var useragn: Long? = null

    @Column(name = "PAROLE", columnDefinition = "unknown")
    open var parole: String? = null

    @Column(name = "NOTE", columnDefinition = "unknown")
    open var note: String? = null

    @Column(name = "DSCBARNUMB", columnDefinition = "unknown")
    open var dscbarnumb: String? = null

    @Column(name = "DSCBARNUMBRFID", columnDefinition = "unknown")
    open var dscbarnumbrfid: String? = null

    @Column(name = "LOGIN_ENABLED", columnDefinition = "unknown")
    open var loginEnabled: Int? = null

    @Column(name = "ZUP_1C_ID", columnDefinition = "unknown")
    open var zup1cId: String? = null

    @Column(name = "PIN", columnDefinition = "unknown")
    open var pin: Int? = null

    @Column(name = "EMAIL", columnDefinition = "unknown")
    open var email: String? = null

}