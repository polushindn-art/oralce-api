package com.example.oracleapi.repository.idspec

import com.example.oracleapi.dto.idspec.IdspecTsdResponse
import com.example.oracleapi.entity.Idspec
import jakarta.persistence.Tuple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface IdspecRepository: JpaRepository<Idspec, Long> {
    // Получить все строки документа (без дополнительных данных)
    fun findByPrnRn(prnId: Long): List<Idspec>

    // Получить строки с номенклатурой (для отображения наименований)
    @Query("SELECT i FROM Idspec i JOIN FETCH i.nomen WHERE i.prn.rn = :prnRn")
    fun findByPrnRnWithNomen(@Param("prnRn") prnRn: Long): List<Idspec>

    // Получить строки с номенклатурой (для отображения наименований)
    @Query( value = """
            SELECT 
                i.rn,
                NVL(n.rn, 0) as nomenId,
                n.nomencode as nomenCode,
                n.article,
                n.nomenname as nomenName,
                i.quant,
                i.summ,
                i.inprice,
                i.storein,
                i.storeout,
                LISTAGG(m.km, ' ') WITHIN GROUP (ORDER BY m.km) as kms
            FROM qreal.idspec i
            LEFT JOIN qreal.nomnlist n ON n.rn = i.nomen
            LEFT JOIN qreal.mark_binding mb ON mb.spec_rn = i.rn
            LEFT JOIN qreal.mark m ON m.rn = mb.prn
            WHERE i.prn = :prnRn
            GROUP BY i.rn, n.rn, n.nomencode, n.article, n.nomenname, 
                     i.quant, i.summ, i.inprice, i.storein, i.storeout
        """,
        nativeQuery = true)
    fun findByPrnRnWithNomen2(@Param("prnRn") prnRn: Long): List<Tuple>

    // С пагинацией (для документов с большим количеством строк)
    @Query("SELECT i FROM Idspec i JOIN FETCH i.nomen WHERE i.prn.rn = :prnRn")
    fun findByPrnIdWithNomen(@Param("prnRn") prnRn: Long, pageable: Pageable): Page<Idspec>

    // Получить количество строк в документе
    fun countByPrnRn(prnRn: Long): Long
}