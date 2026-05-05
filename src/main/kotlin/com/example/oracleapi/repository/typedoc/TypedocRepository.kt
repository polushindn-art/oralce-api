package com.example.oracleapi.repository.typedoc

import com.example.oracleapi.entity.Typedoc
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TypedocRepository : JpaRepository<Typedoc, Long> {

    // Поиск ВСЕХ записей с таким doccode (а не одной)
    fun findByDoccode(doccode: String): List<Typedoc>?  // Изменено с Typedoc? на List<Typedoc>?

    // Проверка существования по мнемокоду
    fun existsByDoccode(doccode: String): Boolean

    // Поиск по мнемокоду и разделу
    @Query("SELECT t FROM Typedoc t WHERE t.doccode = :doccode AND t.divisionEntity.divisioncode = :division")
    fun findByDoccodeAndDivision(@Param("doccode") doccode: String, @Param("division") division: Long): Typedoc?

    // Поиск по названию документа (частичное совпадение)
    @Query("SELECT t FROM Typedoc t WHERE t.docname LIKE %:name%")
    fun findByDocnameContaining(@Param("name") name: String): List<Typedoc>

    // Поиск активных типов документов (учет ведется)
    @Query("SELECT t FROM Typedoc t WHERE t.accounting = 1")
    fun findAllActive(): List<Typedoc>

    //Поиск по Division
    @Query("select t FROM Typedoc t WHERE upper(t.divisionEntity.divisioncode) = upper(:divisioncode)")
    fun findByDivision(@Param("divisioncode") divisioncode: String): List<Typedoc>
}