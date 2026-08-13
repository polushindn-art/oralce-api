package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "TOHEAD", schema = Helper.SCHEME)
open class Tohead {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CRN", nullable = false, insertable = false, updatable = false)
    open var crnEntity: Acatalog? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "DOCTYPE", nullable = false, insertable = false, updatable = false)
    open var doctypeEntity: Typedoc? = null

    @Size(max = 10)
    @NotNull
    @Column(name = "DOCPREF", nullable = false, length = 10)
    open var docpref: String? = null

    @NotNull
    @Column(name = "DOCDATE", nullable = false)
    open var docdate: LocalDate? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "AGNSALE", nullable = false)
    open var agnsale: AgnList? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PBE", nullable = false, insertable = false, updatable = false)
    open var pbeEntity: Pbe? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MANAGER", nullable = false, insertable = false, updatable = false)
    open var managerEntity: Userlist? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CLIENT", nullable = false, insertable = false, updatable = false)
    open var clientEntity: AgnList? = null

    @Size(max = 40)
    @Column(name = "AGNPROXY", length = 40)
    open var agnproxy: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TDISKONT", nullable = false)
    open var tdiskont: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "DISKONT", nullable = false, precision = 17, scale = 5)
    open var diskont: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSRATE", nullable = false, precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMPRICE", nullable = false, precision = 17, scale = 2)
    open var sumprice: BigDecimal? = null

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PAYDOC", insertable = false, updatable = false)
    open var paydocEntity: Paydoc? = null

    @Size(max = 160)
    @Column(name = "NOTE", length = 160)
    open var note: String? = null


    @Column(name = "TOHEAD")
    open var tohead: Long? = null

    @NotNull
    @Column(name = "DOCNUMB", nullable = false, precision = 17, scale = 2)
    open var docnumb: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "SUMDOC", nullable = false, precision = 17, scale = 2)
    open var sumdoc: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TPAY_STATUS", nullable = false)
    open var tpayStatus: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "LOADSUMM", nullable = false, precision = 17, scale = 2)
    open var loadsumm: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CONSIGNEE", insertable = false, updatable = false)
    open var consigneeEntity: AgnList? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "MODIFIED", nullable = false)
    open var modified: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BASESUM", nullable = false, precision = 17, scale = 2)
    open var basesum: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NDSSUM", nullable = false, precision = 17, scale = 2)
    open var ndssum: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PRINTED", nullable = false)
    open var printed: Long? = null

    @Column(name = "IDHEAD")
    open var idhead: Long? = null

    @Column(name = "SPECIALMARK")
    open var specialmark: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TOPERATION", nullable = false)
    open var toperation: Boolean? = null

    @ColumnDefault("null")
    @Column(name = "AGNLIST")
    open var agnlist: Long? = null

    @Column(name = "SUMWEIGHT", precision = 17, scale = 3)
    open var sumweight: BigDecimal? = null

    @Column(name = "SUMVOLUME", precision = 17, scale = 3)
    open var sumvolume: BigDecimal? = null

    @Column(name = "OBJECTS", precision = 17, scale = 2)
    open var objects: BigDecimal? = null

    @Column(name = "CURCLE", precision = 17, scale = 2)
    open var curcle: BigDecimal? = null

    @Column(name = "ISDELIVERY")
    open var isdelivery: Boolean? = null

    @ColumnDefault("null")
    @Column(name = "TICKET")
    open var ticket: Long? = null

    @ColumnDefault("NULL")
    @Column(name = "TICKETTIME")
    open var tickettime: LocalDate? = null

    @ColumnDefault("NULL")
    @Column(name = "ENDDELIVERY")
    open var enddelivery: LocalDate? = null

    @Column(name = "PAYNOW")
    open var paynow: Long? = null

    @Column(name = "EAN13")
    open var ean13: Long? = null

    @ColumnDefault("NULL")
    @Column(name = "NEEDNDS")
    open var neednds: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MANAGER1", insertable = false, updatable = false)
    open var manager1Entity: Userlist? = null

    @Column(name = "COLOR")
    open var color: Long? = null

    @Column(name = "ISURGENTLOAD")
    open var isurgentload: Boolean? = null

    @Column(name = "ISAUTOARSENAL")
    open var isautoarsenal: Boolean? = null

    @Column(name = "FIXDOSTAVKA")
    open var fixdostavka: Boolean? = null

    @Column(name = "KOMPLDATE")
    open var kompldate: LocalDate? = null

    @Column(name = "KONSPRICE")
    open var konsprice: Boolean? = null

    @Column(name = "CONTRACT", precision = 17, scale = 2)
    open var contract: BigDecimal? = null

    @Size(max = 80)
    @Column(name = "CLIENTCOLOR", length = 80)
    open var clientcolor: String? = null

    @Column(name = "TTN_SUPPLIER")
    open var ttnSupplier: Long? = null

    @ColumnDefault("null")
    @Column(name = "RETURNBRAK")
    open var returnbrak: Boolean? = null

}