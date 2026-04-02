package com.example.oracleapi.service.sendmail

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.sendmail.SimpleEmailRequest
import jakarta.persistence.EntityManager
import jakarta.persistence.ParameterMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailService(
    private val entityManager: EntityManager
) {

    @Transactional
    fun sendSimpleEmail(request: SimpleEmailRequest): Long {
        val schema = Helper.SCHEME

        // 1. Вызываем процедуру и получаем RN
        val storedProcedure = entityManager.createStoredProcedureQuery("$schema.PKG_SENDMAIL.SIMPLE_TEXT_WITH_OUT")

        storedProcedure.registerStoredProcedureParameter("title_", String::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("subject_", String::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("recipients_", String::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("message_", String::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("table_rn", Long::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("table_name", String::class.java, ParameterMode.IN)
        storedProcedure.registerStoredProcedureParameter("out_rn", Long::class.java, ParameterMode.OUT)

        storedProcedure.setParameter("title_", request.title)
        storedProcedure.setParameter("subject_", request.subject)
        storedProcedure.setParameter("recipients_", request.recipients)
        storedProcedure.setParameter("message_", request.message)
        storedProcedure.setParameter("table_rn", request.tableRn)
        storedProcedure.setParameter("table_name", request.tableName)

        storedProcedure.execute()

        val mailRn = storedProcedure.getOutputParameterValue("out_rn") as Long

        print("select0")

        // 2. Проверяем результат в PROTOCOL_MAIL
        val sql = "SELECT RESULT, ERRNAME FROM $schema.PROTOCOL_MAIL WHERE RN = :rn"
        val query = entityManager.createNativeQuery(sql)
        query.setParameter("rn", mailRn)

        print("select")

        val result = query.singleResult as Array<*>
        val resultStatus = result[0].toString().toInt()  // 0 - ошибка, 1 - успех
        val errName = result[1] as? String

        print(result[0])
        print(result[1])

        // Если RESULT = 1 - значит ошибка
        if (resultStatus == 0) {
            val errorMessage = errName ?: "Неизвестная ошибка отправки письма"
            throw RuntimeException("Ошибка отправки письма: $errorMessage")
        }

        return mailRn
    }
}