package com.example.oracleapi.entity.mark

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.NamedStoredProcedureQueries
import jakarta.persistence.NamedStoredProcedureQuery
import jakarta.persistence.ParameterMode
import jakarta.persistence.StoredProcedureParameter
import java.sql.Clob

/**
 * Entity с описанием всех процедур пакета PKG_MARK
 */
@Entity
@NamedStoredProcedureQueries(
    // Процедура UPD - обновление метки
    NamedStoredProcedureQuery(
        name = "MarkProcedure.UPD",
        procedureName = "PKG_MARK.UPD",
        parameters = [
            StoredProcedureParameter(mode = ParameterMode.IN, name = "KM_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.IN, name = "JSON_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.IN, name = "TABLE_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.IN, name = "TABLERN_", type = Long::class),
            StoredProcedureParameter(mode = ParameterMode.IN, name = "STATUS_", type = Int::class),
            StoredProcedureParameter(mode = ParameterMode.IN, name = "NOTE_", type = String::class)
        ]
    ),
    // Процедура GET - получение метки
    NamedStoredProcedureQuery(
        name = "MarkProcedure.GET",
        procedureName = "PKG_MARK.GET",
        parameters = [
            StoredProcedureParameter(mode = ParameterMode.IN, name = "KM_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.OUT, name = "JSON_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.OUT, name = "STATUS_", type = Int::class)
        ]
    ),
    // Процедура DEL - удаление метки
    NamedStoredProcedureQuery(
        name = "MarkProcedure.DEL",
        procedureName = "PKG_MARK.DEL",
        parameters = [
            StoredProcedureParameter(mode = ParameterMode.IN, name = "KM_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.OUT, name = "RESULT_", type = Int::class)
        ]
    ),
    // Процедура LIST - список меток
    NamedStoredProcedureQuery(
        name = "MarkProcedure.LIST",
        procedureName = "PKG_MARK.LIST",
        parameters = [
            StoredProcedureParameter(mode = ParameterMode.IN, name = "FILTER_", type = String::class),
            StoredProcedureParameter(mode = ParameterMode.OUT, name = "CURSOR_", type = Void::class)
        ]
    )
)

class MarkProcedureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null
}
