package com.example.oracleapi.service.notification

import com.example.oracleapi.entity.table.Phonebook
import com.example.oracleapi.repository.maxUserAgn.MaxUserAgnRepository
import com.example.oracleapi.repository.phonebook.PhonebookRepository
import com.example.oracleapi.service.max.mainBoth.MainBotMessageService
import com.example.oracleapi.util.PhoneUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BirthdayNotificationService(
    private val phonebookRepository: PhonebookRepository,
    private val maxUserAgnRepository: MaxUserAgnRepository,
    private val mainBotMessageService: MainBotMessageService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 30 9 * * *")
    //@Scheduled(cron = "0 */1 * * * *")
    fun sendBirthdayNotifications() {
        log.info("🎂 Проверка дней рождения...")

        // ✅ 1. Получаем именинников из phonebook
        val today = LocalDate.now()
        val allEmployees = phonebookRepository.findAll()
        val birthdayPeople = allEmployees.filter { employee ->
            employee.rdate?.let { birthDate ->
                birthDate.month == today.month && birthDate.dayOfMonth == today.dayOfMonth
            } ?: false
        }

        if (birthdayPeople.isEmpty()) {
            log.info("🎂 Сегодня дней рождения нет")
            return
        }

        log.info("🎂 Найдено именинников: ${birthdayPeople.size}")

        // ✅ 2. Получаем ВСЕХ активных сотрудников в боте (JOIN)
        val allEmployeeUsers = maxUserAgnRepository.findAllActiveEmployees()

        if (allEmployeeUsers.isEmpty()) {
            log.info("📢 Нет сотрудников, зарегистрированных в боте")
            return
        }

        log.info("👥 Всего сотрудников в боте: ${allEmployeeUsers.size}")

        // ✅ 3. Получаем именинников в боте
        val birthdayUsers = maxUserAgnRepository.findBirthdayEmployees()
        log.info("🎂 Именинников в боте: ${birthdayUsers.size}")

        // ✅ 4. Карта именинников: phoneTail → MaxUserAgn
        val birthdayUserMap = birthdayUsers.associateBy { user ->
            PhoneUtils.getPhoneTail(user.phone ?: "")
        }

        // ✅ 5. Личное поздравление именинникам
        birthdayPeople.forEach { employee ->
            val phoneTail = PhoneUtils.getPhoneTail(employee.phoneSot ?: "")
            val user = birthdayUserMap[phoneTail]

            user?.let {
                try {
                    mainBotMessageService.sendMessage(
                        it.chatId ?: return@forEach,
                        buildPersonalBirthdayMessage(employee),
                        "markdown"
                    )
                    log.info("✅ Личное поздравление отправлено имениннику ${it.userName}")
                } catch (e: Exception) {
                    log.error("❌ Не удалось отправить поздравление имениннику ${it.userId}", e)
                }
            }
        }

        // ✅ 6. Общее уведомление для ВСЕХ сотрудников
        val generalMessage = buildGeneralBirthdayMessage(birthdayPeople)

        allEmployeeUsers.forEach { user ->
            try {
                mainBotMessageService.sendMessage(
                    user.chatId ?: return@forEach,
                    generalMessage,
                    "markdown"
                )
                log.info("✅ Общее уведомление отправлено пользователю ${user.userName}")
            } catch (e: Exception) {
                log.error("❌ Не удалось отправить уведомление пользователю ${user.userId}", e)
            }
        }
    }

    private fun buildPersonalBirthdayMessage(employee: Phonebook): String {
        val fullName = listOfNotNull(employee.fname, employee.nname).joinToString(" ")

        return """
        🎉🎊 *С ДНЁМ РОЖДЕНИЯ!* 🎊🎉

        🥳✨ Поздравляем **$fullName**! ✨🥳

        🌟 Желаем:
        💪 Крепкого здоровья
        😊 Счастья и улыбок
        💰 Финансового благополучия
        🚀 Успехов в работе
        ❤️ Тепла и заботы близких

        🎂 Пусть этот день будет наполнен радостью! 🎁
        """.trimIndent()
    }

    private fun buildGeneralBirthdayMessage(birthdayPeople: List<Phonebook>): String {
        val names = birthdayPeople.joinToString("\n") { employee ->
            listOfNotNull(employee.fname, employee.nname).joinToString(" ")
        }

        return """
🎉 *Сегодня день рождения!* 🎉

🥳 Поздравляем:

$names

🎂 Присоединяйтесь к поздравлениям! 🎁
        """.trimIndent()
    }
}