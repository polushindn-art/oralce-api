package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

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

    @OnDelete(action = OnDeleteAction.RESTRICT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIVISION")
    open var divisionEntity: Division? = null,

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