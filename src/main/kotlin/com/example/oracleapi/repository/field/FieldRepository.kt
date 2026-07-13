package com.example.oracleapi.repository.field

import com.example.oracleapi.entity.table.Field
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface FieldRepository: JpaRepository<Field, Long> {

    // Получить все значения для конкретного поля (без учета регистра)
    @Query("SELECT f FROM Field f WHERE UPPER(f.fieldName) = UPPER(:fieldName) ORDER BY f.fieldValue")
    fun findByFieldNameIgnoreCaseOrderByFieldValue(@Param("fieldName") fieldName: String): List<Field>

    // Получить конкретное значение поля (без учета регистра)
    @Query("SELECT f FROM Field f WHERE UPPER(f.fieldName) = UPPER(:fieldName) AND f.fieldValue = :fieldValue")
    fun findByFieldNameAndFieldValueIgnoreCase(
        @Param("fieldName") fieldName: String,
        @Param("fieldValue") fieldValue: Long?
    ): Field?

    fun findByFieldNameIgnoreCaseOrderByFieldComment(fieldName: String): MutableList<Field>

}