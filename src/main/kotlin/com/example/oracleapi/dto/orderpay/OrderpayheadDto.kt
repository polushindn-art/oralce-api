package com.example.oracleapi.dto.orderpay

import com.example.oracleapi.annotation.BindingDateFormat
import com.example.oracleapi.annotation.BindingDateTimeFormat
import com.example.oracleapi.dto.contract.ContractDto
import com.example.oracleapi.dto.paydoc.PaydocDto
import com.example.oracleapi.entity.table.Orderpayhead
import com.example.oracleapi.entity.table.Orderpayspec
import com.example.oracleapi.entity.table.Paydoc
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.contracts.contract

/**
 * DTO for {@link com.example.oracleapi.entity.table.Orderpayhead}
 */
data class OrderpayheadDto(
    val rn: Long? = null,
    val orderhead: Long? = null,
    @field:NotNull
    val status: Long? = null,
    @field:BindingDateFormat
    val docdate: LocalDate? = null,
    @field:BindingDateFormat
    val plandate: LocalDate? = null,
    val paydoc: Long? = null,
    val payDocEntity: PaydocDto? = null,
    @field:Size(max = 1000)
    val note: String? = null,
    val orderpay: BigDecimal? = null,
    val orderPayCode: String? = null,
    val numCantract: Long? = null,
    val contractEntity: ContractDto? = null,
    val modified: Long? = null,
    val specEntity: List<OrderpayspecDto>? = null
) : Serializable {
    companion object {

        fun fromOrderPayHead(orderpayhead: Orderpayhead): OrderpayheadDto {
            return OrderpayheadDto(
                rn = orderpayhead.rn,
                orderhead = orderpayhead.orderheadEntity?.rn,
                status = orderpayhead.status,
                docdate = orderpayhead.docdate,
                plandate = orderpayhead.plandate,
                paydoc = orderpayhead.paydoc,
                payDocEntity = orderpayhead.paydocEntity?.let { PaydocDto.fromEntity(it) },
                note = orderpayhead.note,
                orderpay = orderpayhead.orderpay,
                orderPayCode = orderpayhead.prepayField?.fieldComment,
                numCantract = orderpayhead.numCantract?.rn,
                contractEntity = orderpayhead.numCantract?.let { ContractDto.fromEntity(it) },
                modified = orderpayhead.modified,
                specEntity = orderpayhead.specEntity.map { OrderpayspecDto.fromEntity(it) }
            )
        }

    }
}