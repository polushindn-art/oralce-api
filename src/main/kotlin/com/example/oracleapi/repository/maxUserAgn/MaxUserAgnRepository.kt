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

    /**
     * Найти всех активных пользователей по списку phoneTail
     */
    @Query("SELECT m FROM MaxUserAgn m WHERE m.phoneTail IN :phoneTails AND m.isActive = :isActive")
    fun findByPhoneTailInAndIsActive(
        @Param("phoneTails") phoneTails: List<String>,
        @Param("isActive") isActive: Boolean
    ): List<MaxUserAgn>

    // ✅ Все активные сотрудники (JOIN)
    @Query("""
        SELECT mua 
        FROM MaxUserAgn mua 
        JOIN Phonebook pb ON pb.phoneTail = mua.phoneTail
        WHERE pb.phoneSot IS NOT NULL 
          AND mua.isActive = true
    """)
    fun findAllActiveEmployees(): List<MaxUserAgn>

    // ✅ Именинники сегодня (JOIN + фильтр по дате)
    @Query("""
    SELECT mua 
    FROM MaxUserAgn mua 
    JOIN Phonebook pb ON pb.phoneTail = mua.phoneTail
    WHERE pb.phoneSot IS NOT NULL 
      AND mua.isActive = true
      AND EXTRACT(MONTH FROM pb.rdate) = EXTRACT(MONTH FROM CURRENT_DATE)
      AND EXTRACT(DAY FROM pb.rdate) = EXTRACT(DAY FROM CURRENT_DATE)
""")
    fun findBirthdayEmployees(): List<MaxUserAgn>

    @Query("""
        SELECT mua 
        FROM MaxUserAgn mua 
        JOIN Phonebook pb ON pb.phoneTail = mua.phoneTail
        WHERE pb.phoneSot IS NOT NULL 
          AND mua.isActive = true
          AND mua.phoneTail = :phoneTail
    """)
    fun findEmployeeByPhoneTail(@Param("phoneTail") phoneTail: String): MaxUserAgn?

}