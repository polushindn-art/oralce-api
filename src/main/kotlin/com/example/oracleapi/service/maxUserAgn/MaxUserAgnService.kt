package com.example.oracleapi.service.maxUserAgn

import com.example.oracleapi.entity.table.MaxUserAgn
import com.example.oracleapi.repository.maxUserAgn.MaxUserAgnRepository
import com.example.oracleapi.service.public.PublicProcedureService
import com.example.oracleapi.util.PhoneUtils
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime

@Service
class MaxUserAgnService(
    private val maxUserAgnRepository: MaxUserAgnRepository,
    private val publicProcedureService: PublicProcedureService
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val phoneUtils = PhoneUtils()

    /**
     * Проверить, зарегистрирован ли уже этот чат
     */
    fun isChatRegistered(chatId: String): Boolean {
        return maxUserAgnRepository.existsByChatId(chatId)
    }

    /**
     * Найти привязку по chat_id
     */
    fun findByChatId(chatId: String): MaxUserAgn? {
        return maxUserAgnRepository.findByChatId(chatId).firstOrNull()
    }

    /**
     * Добавить новую привязку пользователя к номеру
     */
    @Transactional
    fun addUserAgn(
        userId: String,
        chatId: String,
        phone: String,
        botType: String = "MAIN",
        userName: String? = null
    ): MaxUserAgn {
        val phoneTail = phoneUtils.getPhoneTail(phone)

        // Проверяем, существует ли уже привязка
        val existing = maxUserAgnRepository.findByPhoneTailAndBotType(phoneTail, botType)
        if (existing.isNotEmpty()) {
            log.warn("⚠️ Привязка уже существует: phone=$phone, botType=$botType")
            return existing.first()
        }

        // Генерируем RN
        val genIdResponse = publicProcedureService.getIdRn()
        val rn = genIdResponse.rn ?: throw IllegalStateException("Не удалось сгенерировать RN")

        val entity = MaxUserAgn().apply {
            this.rn = rn
            this.userId = userId
            this.chatId = chatId
            this.userName = userName
            this.phone = phone
            this.botType = botType
            this.createdAt = LocalDateTime.now()
            this.updatedAt = LocalDateTime.now()
            this.isActive = true
        }

        val saved = maxUserAgnRepository.save(entity)
        log.info("✅ Добавлена привязка: userId=$userId, phone=$phone, botType=$botType, chatId=$chatId")
        return saved
    }

    /**
     * Найти chat_id по номеру и типу бота
     */
    fun findChatIdByPhoneAndBotType(phone: String, botType: String): String? {
        val phoneTail = phoneUtils.getPhoneTail(phone)
        return maxUserAgnRepository.findChatIdByPhoneAndBotType(phoneTail, botType)
    }

    /**
     * Удалить все привязки по chat_id (отписка)
     */
    @Transactional
    fun deleteByChatId(chatId: String) {
        maxUserAgnRepository.deleteByChatId(chatId)
        log.info("🗑️ Удалены все привязки для chatId=$chatId")
    }

    /**
     * Удалить все привязки пользователя
     */
    @Transactional
    fun deleteByUserId(userId: String) {
        maxUserAgnRepository.deleteByUserId(userId)
        log.info("🗑️ Удалены все привязки для userId=$userId")
    }

    /**
     * Удалить привязку по номеру и типу бота
     */
    @Transactional
    fun deleteByPhoneAndBotType(phone: String, botType: String) {
        val phoneTail = phoneUtils.getPhoneTail(phone)
        maxUserAgnRepository.deleteByPhoneAndBotType(phoneTail, botType)
        log.info("🗑️ Удалена привязка: phone=$phone, botType=$botType")
    }

    /**
     * Проверить, существует ли привязка
     */
    fun existsByPhoneAndBotType(phone: String, botType: String): Boolean {
        val phoneTail = phoneUtils.getPhoneTail(phone)
        return maxUserAgnRepository.existsByPhoneAndBotType(phoneTail, botType)
    }
}