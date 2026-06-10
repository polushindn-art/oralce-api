package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate

@Entity
@Table(name = "MARK_BINDING", schema = Helper.SCHEME)
open class MarkBinding {
    @Id
    @Column(name = "RN", nullable = false)
    open var id: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PRN", nullable = false)
    open var prn: Mark? = null

    @NotNull
    @Column(name = "DOC_RN", nullable = false)
    open var docRn: Long? = null

    @Size(max = 64)
    @NotNull
    @Column(name = "DOC_TABLE_NAME", nullable = false, length = 64)
    open var docTableName: String? = null

    @ColumnDefault("SYSDATE")
    @Column(name = "BINDING_DATE")
    open var bindingDate: LocalDate? = null

}