package com.example.oracleapi.repository.user

import com.example.oracleapi.dto.JsonResponseView
import com.example.oracleapi.dto.tsdlist.UsedJson
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class TsdUsedRepository {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun findTsdUsed(pbe: Long?): List<UsedJson> {

        val query = entityManager.createNativeQuery(
            """
                SELECT userlist, agncode, tsd_sn 
                FROM V_TSDUSED 
                WHERE tsd_pbe = :pbe 
                ORDER BY agncode
                """
        )

        query.setParameter("pbe", pbe)

        val resultList = query.resultList as List<Array<Any>>

        return resultList.map { row ->
            UsedJson(
                (row[0] as? Number)?.toLong(),
                row[1] as? String,
                row[2] as? String
            )
        }
    }
}