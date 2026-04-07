package com.example.oracleapi.service.idhead

import com.example.oracleapi.dto.idhead.status.StatusUpdateRequest
import com.example.oracleapi.exception.DocumentNotFoundException
import com.example.oracleapi.repository.idhead.IdheadRepository
import org.springframework.stereotype.Component
import jakarta.persistence.EntityManager
import jakarta.persistence.ParameterMode
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory

@Component
class UpdateStatus(
    private val entityManager: EntityManager,
    private val idheadRepository: IdheadRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun updateStatus(request: StatusUpdateRequest) {
        log.info("Обновление статуса документа: rn={}, newStatus={}", request.rn, request.newStatus)

        // Проверка существования документа
        if (!idheadRepository.existsByRn(request.rn)) {
            log.warn("Документ с rn={} не найден", request.rn)
            throw DocumentNotFoundException(request.rn)
        }

        try {
            val storedProcedure = entityManager.createStoredProcedureQuery("PKG_IDHEAD.STATUS_UPDATE")

            storedProcedure.registerStoredProcedureParameter("rn_", Long::class.java, ParameterMode.IN)
            storedProcedure.registerStoredProcedureParameter("newstatus", Long::class.java, ParameterMode.IN)

            storedProcedure.setParameter("rn_", request.rn)
            storedProcedure.setParameter("newstatus", request.newStatus)

            storedProcedure.execute()

            log.info("Статус документа {} успешно обновлен на {}", request.rn, request.newStatus)

        } catch (e: Exception) {
            log.error("Ошибка при обновлении статуса документа {}: {}", request.rn, e.message)
            throw RuntimeException("Ошибка обновления статуса документа: ${e.message}", e)
        }
    }
}