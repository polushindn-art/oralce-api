package com.example.oracleapi.repository.maxUserAgn

import com.example.oracleapi.entity.table.MaxUserAgn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MaxUserAgnRepository : JpaRepository<MaxUserAgn, Long> {
    /**
     * Проверить, зарегистрирован ли уже этот chat_id
     */
    fun existsByChatId(chatId: String): Boolean

    /**
     * Найти chat_id по номеру телефона и типу бота
     * (основной метод для отправки уведомлений)
     */
    @Query("""
        SELECT m.chatId 
        FROM MaxUserAgn m 
        WHERE m.phoneTail = :phoneTail 
          AND m.botType = :botType 
          AND m.isActive = true
    """)
    fun findChatIdByPhoneAndBotType(
        @Param("phoneTail") phone: String,
        @Param("botType") botType: String
    ): String?

    /**
     * Найти все записи по номеру телефона и типу бота
     */
    fun findByPhoneTailAndBotType(phone: String, botType: String): List<MaxUserAgn>

    /**
     * Удалить все привязки пользователя
     */
    fun deleteByUserId(userId: String)

    /**
     * Проверить, существует ли привязка
     */
    fun existsByPhoneAndBotType(phone: String, botType: String): Boolean

    /**
     * Удалить привязку по номеру и типу бота
     */
    fun deleteByPhoneAndBotType(phone: String, botType: String)

    fun deleteByChatId(chatId: String)

    fun findByChatId(chatId: String): List<MaxUserAgn>
}