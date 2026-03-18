package com.example.oracleapi.service.procedure

import jakarta.persistence.EntityManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Clob
import java.sql.Connection

abstract class CommonProcedure(protected val em: EntityManager) {
    protected val log: Logger = LoggerFactory.getLogger(this.javaClass)

    protected fun convertToClob(json: String): Clob {
        val conn = em.unwrap(Connection::class.java)
        return conn.createClob().apply {
            setString(1, json)
        }
    }

}