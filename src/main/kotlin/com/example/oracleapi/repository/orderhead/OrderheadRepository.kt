package com.example.oracleapi.repository.orderhead

import com.example.oracleapi.entity.table.Orderhead
import com.example.oracleapi.entity.table.Ordernaklhead
import org.intellij.lang.annotations.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface OrderheadRepository : JpaRepository<Orderhead, Long> {

    fun existsByRn(rn: Long): Boolean

    fun findByRn(rn: Long): Orderhead?

    /*Обновить документы от поставщика*/
    @Modifying
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

    /*Очистить документь от поставщика*/
    @Modifying
    @Query("UPDATE Orderhead o SET o.basisdoctype = NULL, o.basisdocpref = NULL, o.basisdocnumb = NULL, o.basisdocdate = NULL WHERE o.rn = :rn")
    fun clearBasisDoc(@Param("rn") rn: Long): Int

    /*Обновить поставщика*/
    @Modifying
    @Query("UPDATE Orderhead o set o.provider =:provider WHERE o.rn = :rn")
    fun updateProvider(
        @Param("rn") orderhead: Long,
        @Param("provider") provider: Long,
    ): Int

    /*Обновить наше юридическое лицо*/
    @Modifying
    @Query("UPDATE Orderhead o set o.ul = :ul WHERE o.rn = :rn")
    fun updateUl(
        @Param("rn") rn: Long,
        @Param("ul") ul: Long
    ): Int

    /*Обновить склад получения*/
    @Modifying
    @Query("UPDATE Orderhead  o set o.storein = :storein WHERE o.rn = :rn")
    fun updateStorein(
        @Param("rn") rn: Long?,
        @Param("storein") storein: Long?,
    ): Int

    /*Обновить склад получения и наше ЮЛ*/
    @Modifying
    @Query("UPDATE Orderhead  o set o.storein = :storein, o.ul = :ul WHERE o.rn = :rn")
    fun updateStoreinAndUl(
        @Param("rn") rn: Long?,
        @Param("storein") storein: Long?,
        @Param("ul") ul: Long?
    ): Int

    /*Обновить дату прихода*/
    @Modifying
    @Query("update Orderhead o set o.arrivaldate = :arrivaldate WHERE o.rn = :rn")
    fun updateArrivalDate(
        @Param("rn") rn: Long?,
        @Param("arrivaldate") arrivaldate: LocalDate?
    ): Int

    /*Обновить примечание*/
    @Modifying
    @Query("update Orderhead o set o.note = :note WHERE o.rn = :rn")
    fun updateNote(
        @Param("rn") rn: Long?,
        @Param("note") note: String?
    ): Int

}