package com.example.oracleapi.repository.orderspec

import com.example.oracleapi.entity.table.Orderspec
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderspecRepository : JpaRepository<Orderspec, Long> {

    @Modifying
    @Query("update Orderspec o set o.storein = :storein where o.prn = :prn")
    fun updateStoreIn(
        @Param("prn") prn: Long?,
        @Param("storein") storein: Long?,
    ): Int

    fun findByRn(rn: Long): Orderspec?

}