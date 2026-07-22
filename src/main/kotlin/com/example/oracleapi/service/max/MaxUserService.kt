package com.example.oracleapi.service.max

import com.example.oracleapi.entity.table.MaxUser
import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.max.MaxUserRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.public.PublicProcedureService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MaxUserService(
    private val maxUserRepository: MaxUserRepository,
    private val publicProcedureService: PublicProcedureService,
    private val phonebookRepository: PhonebookRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun saveUser(
        internalNumber: String,
        userId: String,
        chatId: String,
        userName: String? = null
    ): MaxUser {
        val genIdResponse = publicProcedureService.getIdRn()
        val rn = genIdResponse.rn ?: throw IllegalStateException("Не удалось сгенерировать RN")

        log.info("📝 Сгенерирован RN=$rn для пользователя $userId")

        val phonebook = phonebookRepository.findByPhoneInt(internalNumber)
        val userName = buildFullName(phonebook)

        log.info("📝 Найден сотрудник: $userName ($internalNumber)")

        val newUser = MaxUser(
            rn = rn,
            internalNumber = internalNumber,
            userId = userId,
            chatId = chatId,
            userName = userName
        )

        log.info("📝 Сохраняем пользователя: internalNumber=$internalNumber, userId=$userId")

        val saved = maxUserRepository.save(newUser)

        // Принудительно сбрасываем в БД
        maxUserRepository.flush()

        log.info("✅ Пользователь сохранен: RN=${saved.rn}, internalNumber=${saved.internalNumber}")

        return saved
    }

    private fun buildFullName(phonebook: Phonebook?): String {
        if (phonebook == null) return "Сотрудник"
        return listOfNotNull(
            phonebook.nname,
            phonebook.fname
        ).joinToString(" ")
    }

    @Transactional(readOnly = true)
    fun findByInternalNumber(internalNumber: String): MaxUser? {
        return maxUserRepository.findByInternalNumber(internalNumber)
    }

    @Transactional(readOnly = true)
    fun findByUserId(userId: String): MaxUser? {
        return maxUserRepository.findByUserId(userId)
    }
}