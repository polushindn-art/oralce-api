package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.prefix.PrefixResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "PREFIX", schema = Helper.SCHEME)
open class Prefix {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @Size(max = 10)
    @Column(name = "DOCPREFNEW", length = 10)
    open var docprefnew: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
     var note: String? = null

    @Column(name = "DIVISION", nullable = false)
    @NotNull
    var division: Long = 0

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIVISION", updatable = false, insertable = false)
    var divisionEntity: Division? = null

    companion object {
        fun toResponse(prefix: Prefix): PrefixResponse {
            return PrefixResponse(
                id = prefix.rn ?: 0,
                docpref = prefix.docpref ?: "",
                docprefnew = prefix.docprefnew,
                note = prefix.note,
                divisionCode = prefix.divisionEntity?.divisioncode
            )
        }
    }

}