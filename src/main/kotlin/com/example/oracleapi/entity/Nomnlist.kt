package com.example.oracleapi.entity

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
@Table(name = "NOMNLIST", schema = Helper.SCHEME)
open class Nomnlist {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "CRN", nullable = false)
    open var crn: Acatalog? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "NOMENCODE", nullable = false, length = 20)
    open var nomencode: String? = null

    @Size(max = 160)
    @NotNull
    @Column(name = "NOMENNAME", nullable = false, length = 160)
    open var nomenname: String? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMENGROUP", nullable = false)
    open var nomengroup: Nomngroup? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MEASURE", nullable = false)
    open var measure: Measure? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "COUNTRY", nullable = false)
    open var country: Country? = null

    @Size(max = 40)
    @Column(name = "GTD", length = 40)
    open var gtd: String? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "BARCODE", nullable = false, length = 40)
    open var barcode: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEPRICE")
    open var typeprice: Typeprice? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "TYPEBAR")
    open var typebar: Typebar? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "PROTECTION", nullable = false)
    open var protection: Long? = null

    @Column(name = "LENGTH", precision = 17, scale = 2)
    open var length: BigDecimal? = null

    @Column(name = "WIDTH", precision = 17, scale = 2)
    open var width: BigDecimal? = null

    @Column(name = "HEIGHT", precision = 17, scale = 2)
    open var height: BigDecimal? = null

    @Column(name = "WEIGHT", precision = 17, scale = 3)
    open var weight: BigDecimal? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @Size(max = 40)
    @Column(name = "PRODUCER", length = 40)
    open var producer: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "NOMENTYPE", nullable = false)
    open var nomentype: Long? = null

    @Size(max = 160)
    @Column(name = "SERT", length = 160)
    open var sert: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISCERTDK", nullable = false)
    open var iscertdk: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISCERTS", nullable = false)
    open var iscerts: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISCERTSE", nullable = false)
    open var iscertse: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISCERTP", nullable = false)
    open var iscertp: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISCERTK", nullable = false)
    open var iscertk: Long? = null

    @Column(name = "CERTDK")
    open var certdk: Long? = null

    @Column(name = "CERTS")
    open var certs: Long? = null

    @Column(name = "CERTSE")
    open var certse: Long? = null

    @Column(name = "CERTP")
    open var certp: Long? = null

    @Column(name = "CERTK")
    open var certk: Long? = null

    @Column(name = "NOMNCAT")
    open var nomncat: Long? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "OVERHAUL", nullable = false)
    open var overhaul: Long? = null

    @NotNull
    @ColumnDefault("-2147483643")
    @Column(name = "COLOR", nullable = false)
    open var color: Long? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "ENABLED", nullable = false)
    open var enabled: Long? = null

    @Size(max = 320)
    @Column(name = "DESCRIPTION", length = 320)
    open var description: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "KOPECK", nullable = false)
    open var kopeck: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ASSORTSPEC")
    open var assortspec: Assortspec? = null

    @Size(max = 40)
    @Column(name = "PBARCODE1", length = 40)
    open var pbarcode1: String? = null

    @Size(max = 40)
    @Column(name = "PBARCODE2", length = 40)
    open var pbarcode2: String? = null

    @Size(max = 40)
    @Column(name = "PBARCODE3", length = 40)
    open var pbarcode3: String? = null

    @Column(name = "CHECKBARCODE")
    open var checkbarcode: Long? = null

    @NotNull
    @ColumnDefault("20")
    @Column(name = "NDSRATE", nullable = false, precision = 17, scale = 2)
    open var ndsrate: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "A4TYPEPRICE")
    open var a4typeprice: Typeprice? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "CHECK_STATUS", nullable = false)
    open var checkStatus: Long? = null

    @Column(name = "CHECKDATE")
    open var checkdate: LocalDate? = null

    @Column(name = "CORRECTDATE")
    open var correctdate: LocalDate? = null

    @Column(name = "AKTPP")
    open var aktpp: Long? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "WHSCONST", nullable = false)
    open var whsconst: Long? = null

    @Column(name = "INSERT_SALE_DATE")
    open var insertSaleDate: LocalDate? = null

    @Column(name = "STOPPRICE", precision = 17, scale = 2)
    open var stopprice: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "EXPIRETYPE")
    open var expiretype: Boolean? = null

    @Column(name = "EXPIRELIFE")
    open var expirelife: Short? = null

    @Column(name = "NO_UCEN")
    open var noUcen: Boolean? = null

    @Column(name = "CHANGE_DAY")
    open var changeDay: Short? = null

    @Column(name = "NOTISE_CHANGE_DAY")
    open var notiseChangeDay: Short? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "STAPLENUMB")
    open var staplenumb: Nomnlist? = null

    @Column(name = "STAPLEDATE")
    open var stapledate: LocalDate? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ARTICLEOC", nullable = false)
    open var articleoc: Boolean? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "AUTOMARGINOC")
    open var automarginoc: Automarginoc? = null

    @Size(max = 6)
    @ColumnDefault("null")
    @Column(name = "KATUPR", length = 6)
    open var katupr: String? = null

    @ColumnDefault("null")
    @Column(name = "MINTORGNAD", precision = 17, scale = 2)
    open var mintorgnad: BigDecimal? = null

    @ColumnDefault("null")
    @Column(name = "NOMENASG")
    open var nomenasg: Long? = null

    @Column(name = "ISFRAGILE")
    open var isfragile: Boolean? = null

    @Column(name = "ISKEEPAWAYFROMFIRE")
    open var iskeepawayfromfire: Boolean? = null

    @Column(name = "ISPROTECTFROMMOISTURE")
    open var isprotectfrommoisture: Boolean? = null

    @Column(name = "ISSTOREDVERTICALLY")
    open var isstoredvertically: Boolean? = null

    @Column(name = "ISKEEPWARM")
    open var iskeepwarm: Boolean? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOTCONDITION")
    open var notcondition: Nomnlist? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "PACKAGING")
    open var packaging: Packaging? = null

    @ColumnDefault("0")
    @Column(name = "CHECKOBOROT")
    open var checkoborot: Boolean? = null

    @ColumnDefault("0")
    @Column(name = "MAXNORMTURNOVER", precision = 17, scale = 2)
    open var maxnormturnover: BigDecimal? = null

    @Column(name = "LOGISTIC_KAT", precision = 17, scale = 2)
    open var logisticKat: BigDecimal? = null

    @Column(name = "SUMMZAKUP", precision = 17, scale = 2)
    open var summzakup: BigDecimal? = null

    @Column(name = "DATAVOZVR")
    open var datavozvr: LocalDate? = null

    @Column(name = "REPEAT_SALE_DATE")
    open var repeatSaleDate: LocalDate? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @ColumnDefault("null")
    @JoinColumn(name = "MINIGROUP")
    open var minigroup: Minigroup? = null

    @Column(name = "PARENT")
    open var parent: Long? = null

    @ColumnDefault("0")
    @Column(name = "MINRESERVESTOP")
    open var minreservestop: Boolean? = null

    @ColumnDefault("0")
    @Column(name = "PRCDOCINTURNOVER")
    open var prcdocinturnover: Boolean? = null

    @Size(max = 40)
    @Column(name = "PBARCODE4", length = 40)
    open var pbarcode4: String? = null

    @Size(max = 40)
    @Column(name = "PBARCODE5", length = 40)
    open var pbarcode5: String? = null

    @ColumnDefault("NULL")
    @Column(name = "CRN2")
    open var crn2: Long? = null

    @Size(max = 17)
    @Column(name = "ARTICLE", length = 17)
    open var article: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "SKU")
    open var sku: Sku? = null

    @Column(name = "BASICFORSKU")
    open var basicforsku: Long? = null

    @ColumnDefault("NULL")
    @Column(name = "DAL", precision = 17, scale = 2)
    open var dal: BigDecimal? = null

    @ColumnDefault("NULL")
    @Column(name = "KOD_VID_TOV")
    open var kodVidTov: Long? = null

    @ColumnDefault("NULL")
    @Column(name = "TOV_POST", precision = 17, scale = 2)
    open var tovPost: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "ALCOHOL")
    open var alcohol: Boolean? = null

    @Size(max = 25)
    @ColumnDefault("null")
    @Column(name = "COLORHINT", length = 25)
    open var colorhint: String? = null

    @Column(name = "SANCTION")
    open var sanction: Long? = null

    @Size(max = 40)
    @Column(name = "PBARCODE6", length = 40)
    open var pbarcode6: String? = null

    @Size(max = 40)
    @Column(name = "PBARCODE7", length = 40)
    open var pbarcode7: String? = null

    @Size(max = 40)
    @Column(name = "PBARCODE8", length = 40)
    open var pbarcode8: String? = null

    @Size(max = 40)
    @Column(name = "PBARCODE9", length = 40)
    open var pbarcode9: String? = null

    @Size(max = 20)
    @ColumnDefault("NULL")
    @Column(name = "MARTICLE", length = 20)
    open var marticle: String? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "ISCERTOP", nullable = false)
    open var iscertop: Long? = null

    @Column(name = "CERTOP")
    open var certop: Long? = null

    @ColumnDefault("null")
    @Column(name = "MIN_REMAINED_LIFE")
    open var minRemainedLife: Short? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ColumnDefault("null")
    @JoinColumn(name = "MARK_TYPE_GROUP")
    open var markTypeGroup: MarkIntegration? = null

}