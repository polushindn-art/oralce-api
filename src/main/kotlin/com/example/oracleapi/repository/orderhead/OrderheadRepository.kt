package com.example.oracleapi.repository.orderhead

import com.example.oracleapi.entity.Orderhead
import org.intellij.lang.annotations.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface OrderheadRepository : JpaRepository<Orderhead, Long> {

    @Modifying
    @Transactional
    @Language("SQL")
    @Query(
        """
            update Orderhead o 
            set o.basisdoctype = :basisdoctype,
                o.basisdocpref = :basisdocpref,
                o.basisdocnumb = :basisdocnumb,
                o.basisdocdate = :basisdocdate
            where o.rn = :rn
        """
    )
    fun updateBasisdoc(
        @Param("rn") rn: Long,
        @Param("basisdoctype") basisdoctype: Long,
        @Param("basisdocpref") basisdocpref: String,
        @Param("basisdocnumb") basisdocnumb: Long,
        @Param("basisdocdate") basisdocdate: LocalDate
    ): Int

    @Modifying
    @Transactional
    @Query("UPDATE Orderhead o SET o.basisdoctype = NULL, o.basisdocpref = NULL, o.basisdocnumb = NULL, o.basisdocdate = NULL WHERE o.rn = :rn")
    fun clearBasisDoc(@Param("rn") rn: Long): Int

}