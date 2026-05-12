package com.example.oracleapi.service.sendmail

import com.example.oracleapi.Helper
import com.example.oracleapi.dto.sendmail.SimpleEmailRequest
import com.example.oracleapi.service.ProtocolMailService
import com.example.oracleapi.service.mark.MarkProcedureService
import jakarta.persistence.EntityManager
import jakarta.persistence.ParameterMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmailService(
    private val entityManager: EntityManager,
    private val protocolMailService: ProtocolMailService
) {

    @Transactional
    fun sendSimpleEmail(request: SimpleEmailRequest): Long {

        // 1. Вызываем процедуру и получаем RN
        val storedProcedure = entityManager.createStoredProcedureQuery("${Helper.SCHEME}.PKG_SENDMAIL.SIMPLE_TEXT_WITH_OUT")

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

        val resultStatus = protocolMailService.getInfoByRn(mailRn)

        // Если RESULT = 1 - значит ошибка
        if (resultStatus.result == 0L) {
            val errorMessage = resultStatus.errName ?: "Неизвестная ошибка отправки письма"
            throw RuntimeException("Ошибка отправки письма: $errorMessage")
        }

        return mailRn
    }
}