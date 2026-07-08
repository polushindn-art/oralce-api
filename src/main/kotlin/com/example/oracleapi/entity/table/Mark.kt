package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDate

@Entity
@Table(name = "MARK", schema = Helper.SCHEME)
open class Mark {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "DATE_ADD", nullable = false)
    open var dateAdd: LocalDate? = null

    @Size(max = 256)
    @NotNull
    @Column(name = "KM", nullable = false, length = 256)
    open var km: String? = null

    @Column(name = "STATUS")
    open var status: Short? = null

    @ColumnDefault("NULL")
    @Column(name = "STATE_MARK")
    open var stateMark: LocalDate? = null

    @NotNull
    @Lob
    @Column(name = "JSON", nullable = false)
    open var json: String? = null

    @Size(max = 256)
    @Column(name = "CIS", length = 256, insertable = false, updatable = false)
    open var cis: String? = null

    @Size(max = 14)
    @Column(name = "GTIN", length = 14, insertable = false, updatable = false)
    open var gtin: String? = null

}