package com.example.oracleapi.entity.typedoc

import com.example.oracleapi.Helper
import jakarta.persistence.*

@Entity
@Table(name = "TYPEDOC", schema = Helper.SCHEME)
class Typedoc(
    @Id
    @Column(name = "RN", nullable = false)
    var rn: Long = 0,

    @Column(name = "DOCCODE", nullable = false, length = 20)
    var doccode: String = "",

    @Column(name = "DOCNAME", nullable = false, length = 80)
    var docname: String = "",

    @Column(name = "DIVISION", nullable = false)
    var division: Long = 0,

    @Column(name = "NOTE", length = 80)
    var note: String? = null,

    @Column(name = "UNDNUMB", nullable = false)
    var undnumb: Long = 0,

    @Column(name = "DOCTYPE")
    var doctype: Long? = null,

    @Column(name = "WAY_STATUS")
    var wayStatus: Long? = null,

    @Column(name = "PAYDOCTYPE")
    var paydoctype: Long? = null,

    @Column(name = "ACCOUNTING", nullable = false)
    var accounting: Long = 1,

    @Column(name = "DEPTKKMNUMB")
    var deptkkmnumb: Long? = null,

    @Column(name = "RETURNDOCTYPE")
    var returndoctype: Long? = null,

    @Column(name = "ISTEMPLATE", nullable = false)
    var istemplate: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Typedoc
        return rn == other.rn
    }
    override fun hashCode(): Int = rn.hashCode()
}