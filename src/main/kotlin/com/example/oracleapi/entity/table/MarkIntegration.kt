package com.example.oracleapi.entity.table

import com.example.oracleapi.Helper
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDate

@Entity
@Table(name = "MARK_INTEGRATION", schema = Helper.SCHEME)
open class MarkIntegration {
    @Id
    @Column(name = "RN", nullable = false)
    open var rn: Long? = null

    @Size(max = 128)
    @Column(name = "TYPE_PRODUCT", length = 128)
    open var typeProduct: String? = null

    @Size(max = 32)
    @Column(name = "TYPE_PARAMETR", length = 32)
    open var typeParametr: String? = null

    @ColumnDefault("sysdate")
    @Column(name = "WITHDRAWN_DATE")
    open var withdrawnDate: LocalDate? = null

    @ColumnDefault("sysdate")
    @Column(name = "BAN_DATE")
    open var banDate: LocalDate? = null

    @ColumnDefault("sysdate")
    @Column(name = "RETAIL_SALE_UR")
    open var retailSaleUr: LocalDate? = null

    @ColumnDefault("sysdate")
    @Column(name = "RETAIL_SALE_RR")
    open var retailSaleRr: LocalDate? = null

    @ColumnDefault("sysdate")
    @Column(name = "RETAIL_SALE_LM")
    open var retailSaleLm: LocalDate? = null

    @ColumnDefault("sysdate")
    @Column(name = "IS_WHOLESALE_OSU")
    open var isWholesaleOsu: LocalDate? = null

    @ColumnDefault("sysdate")
    @Column(name = "IS_WHOLESALE_ONE")
    open var isWholesaleOne: LocalDate? = null

    @Column(name = "GROUP_ID")
    open var groupId: Long? = null

}