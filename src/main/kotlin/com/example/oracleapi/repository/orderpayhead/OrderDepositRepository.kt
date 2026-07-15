package com.example.oracleapi.repository.orderpayhead

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.orderpay.OrderDepositResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class OrderDepositRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    private val rowMapper = RowMapper<OrderDepositResponse> { rs: ResultSet, _: Int ->
        OrderDepositResponse(
            ohrn = rs.getLong("ohrn"),
            provider = rs.getLong("provider"),
            nomengroup = rs.getLong("nomengroup"),
            groupcode = rs.getString("groupcode"),
            sumprprice = rs.getBigDecimal("sumprprice"),
            prihordSumm = rs.getBigDecimal("prihord_summ"),
            sumspec = rs.getBigDecimal("sumspec"),
            persent = rs.getBigDecimal("persent"),
            paidFor = rs.getBigDecimal("paid_for"),
            remaining = rs.getBigDecimal("remaining"),
            remainingGroup = rs.getBigDecimal("remaining_group"),
            overpayment = rs.getBigDecimal("overpayment"),
            overpaymentGroup = rs.getBigDecimal("overpayment_group"),
            numContractGroup = rs.getString("num_contract_group"),
            contractrn = rs.getLong("contractrn").takeIf { !rs.wasNull() },
            orderpay = rs.getBigDecimal("orderpay")?.takeIf { !rs.wasNull() },
            depositGroup = rs.getBigDecimal("deposit_group")
        )
    }

    fun getOrderDeposit(orderRn: Long, deposit: BigDecimal): List<OrderDepositResponse> {
        val sql = """
            SELECT * FROM TABLE(
                ${Helper.SCHEME}.GET_ORDER_DEPOSIT(?, ?)
            )
        """
        return jdbcTemplate.query(sql, rowMapper, orderRn, deposit)
    }

}