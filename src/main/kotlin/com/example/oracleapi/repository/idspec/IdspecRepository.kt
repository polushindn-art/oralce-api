package com.example.oracleapi.repository.idspec

import com.example.oracleapi.entity.Idhead
import com.example.oracleapi.entity.Idspec
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

    // С пагинацией (для документов с большим количеством строк)
    @Query("SELECT i FROM Idspec i JOIN FETCH i.nomen WHERE i.prn.rn = :prnRn")
    fun findByPrnIdWithNomen(@Param("prnRn") prnRn: Long, pageable: Pageable): Page<Idspec>

    // Получить количество строк в документе
    fun countByPrnRn(prnRn: Long): Long
}