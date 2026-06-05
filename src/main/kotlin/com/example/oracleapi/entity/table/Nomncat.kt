package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "NOMNCAT", schema = Helper.SCHEME)
open class Nomncat {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "CATCODE", nullable = false, length = 20)
    open var catcode: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "CHECKING", nullable = false)
    open var checking: Long? = null

    @ColumnDefault("1")
    @Column(name = "ENABLED")
    open var enabled: Long? = null

    @Column(name = "CATTYPE")
    open var cattype: Long? = null

}