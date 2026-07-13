package com.example.oracleapi.repository.orderpayhead

import com.example.oracleapi.entity.table.Orderpayhead
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderpayheadRepository : JpaRepository<Orderpayhead, Long> {
    fun findOrderPayHeadByOrderhead(orderRn: Long): List<Orderpayhead>

    @Query(
        """
        select oh
        from Orderpayhead oh
        LEFT JOIN FETCH oh.paydocEntity
        where oh.orderhead = :orderRn
    """
    )
    fun findOrderpayheadByOrderheadEntityPayDoc(@Param("orderRn") orderRn: Long): List<Orderpayhead>

}