package com.example.oracleapi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "CART")
open class Cart {
    @Id
    @Column(name = "RN", columnDefinition = "unknown")
    open var rn: Any? = null

    @Column(name = "SPEC", columnDefinition = "unknown")
    open var spec: Any? = null

    @Column(name = "IDCART", columnDefinition = "unknown")
    open var idcart: Any? = null

    @Column(name = "QUANT", columnDefinition = "unknown")
    open var quant: Any? = null

    @Column(name = "USERTSD", columnDefinition = "unknown")
    open var usertsd: Any? = null

    @Column(name = "DATETIMETSD", columnDefinition = "unknown")
    open var datetimetsd: Any? = null

    @Column(name = "RACKZONE", columnDefinition = "unknown")
    open var rackzone: Any? = null

    @Column(name = "NOMENTYPE", columnDefinition = "unknown")
    open var nomentype: Any? = null

    @Column(name = "PARTIONS", columnDefinition = "unknown")
    open var partions: Any? = null

    @Column(name = "DIVISION", columnDefinition = "unknown")
    open var division: Any? = null

    @Column(name = "NOMEN", columnDefinition = "unknown")
    open var nomen: Any? = null

}