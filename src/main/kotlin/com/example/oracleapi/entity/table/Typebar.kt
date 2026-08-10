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
@Table(name = "TYPEBAR", schema = Helper.SCHEME)
open class Typebar {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 20)
    @NotNull
    @Column(name = "BARCODE", nullable = false, length = 20)
    open var barcode: String? = null

    @Size(max = 80)
    @NotNull
    @Column(name = "BARNAME", nullable = false, length = 80)
    open var barname: String? = null

    @Size(max = 80)
    @Column(name = "NOTE", length = 80)
    open var note: String? = null

    @Size(max = 40)
    @NotNull
    @Column(name = "LPT", nullable = false, length = 40)
    open var lpt: String? = null

    @NotNull
    @ColumnDefault("203")
    @Column(name = "RESOLUTION", nullable = false)
    open var resolution: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "LWIDTH", nullable = false)
    open var lwidth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "LLENGTH", nullable = false)
    open var llength: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "LLENGTHGAP", nullable = false)
    open var llengthgap: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE1_STARTH", nullable = false)
    open var strbefore1Starth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE1_STARTV", nullable = false)
    open var strbefore1Startv: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE1_FONT", nullable = false)
    open var strbefore1Font: Boolean? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE2_STARTH", nullable = false)
    open var strbefore2Starth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE2_STARTV", nullable = false)
    open var strbefore2Startv: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE2_FONT", nullable = false)
    open var strbefore2Font: Boolean? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE3_STARTH", nullable = false)
    open var strbefore3Starth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE3_STARTV", nullable = false)
    open var strbefore3Startv: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRBEFORE3_FONT", nullable = false)
    open var strbefore3Font: Boolean? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BARCODE_STARTH", nullable = false)
    open var barcodeStarth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BARCODE_STARTV", nullable = false)
    open var barcodeStartv: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BARCODE_WIDTH", nullable = false)
    open var barcodeWidth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BARCODE_HEIGHT", nullable = false)
    open var barcodeHeight: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "BARCODE_NBWIDTH", nullable = false)
    open var barcodeNbwidth: Boolean? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRAFTER_STARTH", nullable = false)
    open var strafterStarth: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRAFTER_STARTV", nullable = false)
    open var strafterStartv: Short? = null

    @NotNull
    @ColumnDefault("0")
    @Column(name = "STRAFTER_FONT", nullable = false)
    open var strafterFont: Boolean? = null

}