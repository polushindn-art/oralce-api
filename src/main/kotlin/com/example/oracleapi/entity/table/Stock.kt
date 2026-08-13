package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.math.BigDecimal
import java.time.LocalDate
import com.example.oracleapi.dto.stock.StockInfoDto

@Entity
@Table(name = "STOCK", schema = Helper.SCHEME)
@SqlResultSetMapping(
    name = "StockInfoMapping",
    classes = [
        ConstructorResult(
            targetClass = StockInfoDto::class,
            columns = [
                ColumnResult(name = "nomenName", type = String::class),
                ColumnResult(name = "storeCode", type = String::class),
                ColumnResult(name = "nomenId", type = Long::class),
                ColumnResult(name = "storeId", type = Long::class),
                ColumnResult(name = "quantToSale", type = BigDecimal::class),
                ColumnResult(name = "pbeCode", type = String::class),
                ColumnResult(name = "storePbe", type = Long::class),
                ColumnResult(name = "price", type = BigDecimal::class),
                ColumnResult(name = "priceOut", type = BigDecimal::class)
            ]
        )
    ]
)
open class Stock {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @NotNull
    @Column(name = "STORE", nullable = false)
    var store: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "STORE", nullable = false, insertable = false, updatable = false)
    open var storeEntity: Store? = null

    @Column(name = "MAXARTSUP", precision = 17, scale = 3)
    open var maxartsup: BigDecimal? = null

    @Column(name = "OPTARTSUP", precision = 17, scale = 3)
    open var optartsup: BigDecimal? = null

    @Column(name = "TIMEDELIVERY", precision = 17, scale = 1)
    open var timedelivery: BigDecimal? = null

    @Column(name = "TIMESUPPLY", precision = 17, scale = 1)
    open var timesupply: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "QUANTTOSALE", nullable = false, precision = 17, scale = 3)
    open var quanttosale: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "QUANTRESERV", nullable = false, precision = 17, scale = 3)
    open var quantreserv: BigDecimal? = null

    @NotNull
    @Column(name = "nomen", nullable = false)
    var nomen: BigDecimal? = null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "NOMEN", nullable = false, insertable = false, updatable = false)
    open var nomenEntity: Nomnlist? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "QUANTTRANSOUT", nullable = false, precision = 17, scale = 3)
    open var quanttransout: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "QUANTTRANSIN", nullable = false, precision = 17, scale = 3)
    open var quanttransin: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "QUANTRETURN", nullable = false, precision = 17, scale = 3)
    open var quantreturn: BigDecimal? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "MOL", insertable = false, updatable = false)
    open var mol: AgnList? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "AVGPRICE", nullable = false, precision = 17, scale = 10)
    open var avgprice: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "QUANTBREAK", nullable = false, precision = 17, scale = 3)
    open var quantbreak: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "AVGQUANTOUT", nullable = false, precision = 17, scale = 3)
    open var avgquantout: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "TURNOVERRATIO", nullable = false, precision = 17, scale = 5)
    open var turnoverratio: BigDecimal? = null

    @Column(name = "LASTDATEIN")
    open var lastdatein: LocalDate? = null

    @Column(name = "SNSUPPLY", precision = 17, scale = 3)
    open var snsupply: BigDecimal? = null

    @Column(name = "SNDATE")
    open var sndate: LocalDate? = null

    @Size(max = 80)
    @Column(name = "LOCNAME", length = 80)
    open var locname: String? = null

    @NotNull
    @ColumnDefault("1")
    @Column(name = "NOMENSTORE", nullable = false)
    open var nomenstore: Long? = null

    @Column(name = "INROAD", precision = 17, scale = 3)
    open var inroad: BigDecimal? = null

    @Column(name = "INROADDATE")
    open var inroaddate: LocalDate? = null

    @ColumnDefault("null")
    @Column(name = "QUANTARTSTUP", precision = 17, scale = 3)
    open var quantartstup: BigDecimal? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "AVGSUMMOUT", nullable = false, precision = 17, scale = 2)
    open var avgsummout: BigDecimal? = null

    @Column(name = "DELIVERY", precision = 17, scale = 3)
    open var delivery: BigDecimal? = null

    @ColumnDefault("null")
    @Column(name = "ARTSUPDATE")
    open var artsupdate: LocalDate? = null

    @ColumnDefault("0")
    @Column(name = "RAZBORDEFECT", precision = 17, scale = 3)
    open var razbordefect: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "RAZBORSHORTAGE", precision = 17, scale = 3)
    open var razborshortage: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "RAZBORSURPLUS", precision = 17, scale = 3)
    open var razborsurplus: BigDecimal? = null

    @ColumnDefault("0")
    @Column(name = "SURPLUSDEFECT", precision = 17, scale = 3)
    open var surplusdefect: BigDecimal? = null

    @Column(name = "ACCEPTED", precision = 17, scale = 3)
    open var accepted: BigDecimal? = null

    @Column(name = "ACCEPTEDBREAK", precision = 17, scale = 3)
    open var acceptedbreak: BigDecimal? = null

    @Column(name = "ISORDERPACK")
    open var isorderpack: Long? = null

    @ColumnDefault("0")
    @Column(name = "QUANTASX")
    open var quantasx: Long? = null

    @ColumnDefault("0")
    @Column(name = "QUANTDISCH")
    open var quantdisch: Long? = null

    @ColumnDefault("0")
    @Column(name = "QUANTIZLISH")
    open var quantizlish: Long? = null

    @ColumnDefault("0")
    @Column(name = "QUANTWWW")
    open var quantwww: Long? = null
}