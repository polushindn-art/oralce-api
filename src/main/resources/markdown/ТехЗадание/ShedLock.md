# Инструкция по внедрению ShedLock для защиты от дублирующихся задач

## Для чего это нужно

Когда приложение запущено в нескольких экземплярах (например, dev + prod, или два prod для отказоустойчивости), задачи по расписанию (`@Scheduled`) могут выполняться одновременно на всех экземплярах. Это приводит к дублированию действий: пользователь получает два одинаковых уведомления, две записи в БД и т.д.

**ShedLock** решает эту проблему — задача выполняется только на одном экземпляре, остальные пропускают выполнение.

---

## Что нужно сделать

### 1. Добавить зависимости в `pom.xml`

```xml
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-spring</artifactId>
    <version>5.14.0</version>
</dependency>
<dependency>
    <groupId>net.javacrumbs.shedlock</groupId>
    <artifactId>shedlock-provider-jdbc</artifactId>
    <version>5.14.0</version>
</dependency>
```

### 2. Создать таблицу в Oracle

```sql
CREATE TABLE SHEDLOCK (
    name      VARCHAR2(64) PRIMARY KEY,
    lock_until TIMESTAMP NOT NULL,
    locked_at  TIMESTAMP NOT NULL,
    locked_by  VARCHAR2(255) NOT NULL
);
```

### 3. Создать конфигурацию ShedLock

**`config/ShedLockConfig.kt`**

```kotlin
package com.example.oracleapi.config

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
class ShedLockConfig(
    private val dataSource: DataSource
) {

    @Bean
    fun lockProvider(): LockProvider {
        return JdbcLockProvider(dataSource)
    }
}
```

### 4. Добавить аннотацию к задаче

```kotlin
@Scheduled(cron = "0 30 9 * * *")
@SchedulerLock(
    name = "birthdayNotification",
    lockAtLeastFor = "PT5M",
    lockAtMostFor = "PT10M"
)
fun sendBirthdayNotifications() {
    // задача выполнится только на одном экземпляре
}
```

---

## Параметры аннотации `@SchedulerLock`

| Параметр | Описание | Пример |
|---|---|---|
| `name` | Уникальное имя задачи | `"birthdayNotification"` |
| `lockAtLeastFor` | Минимальное время удержания замка (защита от быстрых перезапусков) | `"PT5M"` (5 минут) |
| `lockAtMostFor` | Максимальное время удержания замка (страховка от падения) | `"PT10M"` (10 минут) |

**Формат времени:** `PT` + число + `S`/`M`/`H`

| Значение | Расшифровка |
|---|---|
| `PT30S` | 30 секунд |
| `PT5M` | 5 минут |
| `PT10M` | 10 минут |
| `PT1H` | 1 час |

---

## Где применяется

| Задача | Нужен ShedLock | Почему |
|---|---|---|
| Поздравления с ДР | Да | Один раз в день, дубли недопустимы |
| Напоминалки | Да | Один раз в день |
| Очистка кэша | Да | Достаточно одного экземпляра |
| Отчёт по почте | Да | Один раз в день |
| AMI слушатель | Нет | События приходят на оба экземпляра |

---

## Как это работает

1. В 9:30 оба экземпляра пытаются получить замок `birthdayNotification`
2. Первый экземпляр захватывает замок → выполняет задачу
3. Второй экземпляр видит, что замок занят → пропускает выполнение
4. Через 10 минут замок освобождается

---

## Проверка

### В логах должно быть:

```
Захвачен замок: birthdayNotification
Проверка дней рождения...
Поздравления отправлены!
Замок освобождён: birthdayNotification
```

### Проверка в БД:

```sql
SELECT * FROM SHEDLOCK;
```

---

## Итог

| Что было                           | Что стало        |
|------------------------------------|------------------|
| Два экземпляра → два уведомления   | Одно уведомление |
| Два экземпляра → две записи в БД   | Одна запись      |
| Риск дублей при отказоустойчивости | Защита от дублей |