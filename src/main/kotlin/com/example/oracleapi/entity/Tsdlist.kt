package com.example.oracleapi.entity

import com.example.oracleapi.Helper
import com.example.oracleapi.entity.table.Pbe
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "TSDLIST", schema = Helper.SCHEME)
open class Tsdlist {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 50)
    @NotNull
    @Column(name = "SN", nullable = false, length = 50)
    open var sn: String? = null

    @Size(max = 50)
    @NotNull
    @Column(name = "RFID", nullable = false, length = 50)
    open var rfid: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PBE")
    open var pbe: Pbe? = null

    @Size(max = 255)
    @Column(name = "NOTE")
    open var note: String? = null

    @Size(max = 20)
    @ColumnDefault("null")
    @Column(name = "TSDPROGRAM", length = 20)
    open var tsdprogram: String? = null

    @Size(max = 20)
    @ColumnDefault("null")
    @Column(name = "CURVERSION", length = 20)
    open var curversion: String? = null

    @Size(max = 20)
    @ColumnDefault("null")
    @Column(name = "NEWVERSION", length = 20)
    open var newversion: String? = null

    @Size(max = 40)
    @ColumnDefault("null")
    @Column(name = "TSDIP", length = 40)
    open var tsdip: String? = null

    @Size(max = 40)
    @ColumnDefault("null")
    @Column(name = "TSDMAC", length = 40)
    open var tsdmac: String? = null

    @ColumnDefault("null")
    @Column(name = "DATESTART")
    open var datestart: LocalDateTime? = null

    @Size(max = 100)
    @ColumnDefault("null")
    @Column(name = "TSDNAME", length = 100)
    open var tsdname: String? = null

    @Size(max = 256)
    @ColumnDefault("null")
    @Column(name = "DEVICEID", length = 256)
    open var deviceid: String? = null

    @ColumnDefault("0")
    @Column(name = "VERSIONCODE")
    open var versioncode: Long? = null

    @ColumnDefault("0")
    @Column(name = "UPDATEVERSION")
    open var updateversion: Long? = null

    @ColumnDefault("null")
    @Column(name = "DELETED")
    open var deleted: LocalDate? = null

    // Связь с параметрами (один терминал → много параметров)
    @OneToMany(mappedBy = "prn", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var params: MutableList<Tsdparam> = mutableListOf()
}