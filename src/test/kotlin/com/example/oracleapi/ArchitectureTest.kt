package com.example.oracleapi

import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.domain.properties.HasModifiers.Predicates.modifier
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

/**
 * Набор архитектурных тестов (ArchUnit) для автоматического контроля стандартов разработки
 * в проекте OracleAPI. Тесты проверяют изоляцию слоев, правила транзакций, именование компонентов
 * и предотвращают типичные архитектурные ошибки на этапе CI/CD сборки.
 */
@AnalyzeClasses(packages = ["com.example.oracleapi"])
class ArchitectureTest {

    /**
     * 1. Контроль слоистой архитектуры (Layered Architecture).
     * Определяет строгие границы между компонентами системы:
     * - Контроллеры принимают HTTP-запросы и могут обращаться только к сервисам.
     * - Сервисы содержат бизнес-логику и могут использовать репозитории.
     * - Репозитории работают с БД и изолированы от контроллеров.
     */
    @Suppress("unused")
    @ArchTest
    val layerDependenciesAreRespected: ArchRule = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Controller").definedBy("..controller..")
        .layer("Service").definedBy("..service..")
        .layer("Repository").definedBy("..repository..")
        .layer("Config").definedBy("..config..")
        .layer("Exception").definedBy("..exception..")
        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Config", "Exception")
        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service", "Config")

    /**
     * 2. Стандартизация суффиксов контроллеров.
     * Все классы, помеченные аннотацией `@RestController`, обязаны иметь суффикс `Controller`
     * для единообразия поиска и навигации по коду.
     */
    @Suppress("unused")
    @ArchTest
    val controllersShouldBeSuffixed: ArchRule = classes()
        .that().areAnnotatedWith(RestController::class.java)
        .should().haveSimpleNameEndingWith("Controller")
        .because("Все классы с аннотацией @RestController обязаны заканчиваться на суффикс 'Controller'.")

    /**
     * 3. Стандартизация суффиксов сервисов.
     * Все классы бизнес-логики с аннотацией `@Service` должны иметь суффикс `Service`.
     */
    @Suppress("unused")
    @ArchTest
    val servicesShouldBeSuffixed: ArchRule = classes()
        .that().areAnnotatedWith(Service::class.java)
        .should().haveSimpleNameEndingWith("Service")
        .because("Все компоненты с аннотацией @Service обязаны заканчиваться на суффикс 'Service'.")

    /**
     * 4. Защита API от утечки персистентных моделей.
     * Контроллеры не имеют права напрямую возвращать или принимать сущности базы данных (`..entity..`).
     * Взаимодействие с внешним миром должно происходить строго через DTO-объекты.
     */
    @Suppress("unused")
    @ArchTest
    val controllersShouldNotDependOnEntities: ArchRule = noClasses()
        .that().resideInAPackage("..controller..")
        .should().dependOnClassesThat()
        .resideInAPackage("..entity..")
        .because("Контроллеры не имеют права работать с сущностями БД напрямую! Используйте DTO.")

    /**
     * 5. Запрет транзакций на уровне веб-контроллеров.
     * Открытие транзакций в контроллерах приводит к проблемам с удержанием соединений (Connection Leaks)
     * и нарушает границы ответственности. Транзакции должны управляться строго на уровне сервисов.
     */
    @Suppress("unused")
    @ArchTest
    val controllersShouldNotBeTransactional: ArchRule = classes()
        .that().resideInAPackage("..controller..")
        .should().notBeAnnotatedWith(Transactional::class.java)
        .because("Контроллеры не имеют права управлять транзакциями. Открытие сессий должно происходить строго на сервисном уровне.")

    /**
     * 6. Единая структура глобальных обработчиков ошибок.
     * Классы, перехватывающие исключения (`@ControllerAdvice` / `@RestControllerAdvice`),
     * должны быть сосредоточены в пакете `exception` и заканчиваться на `Advice`.
     */
    @Suppress("unused")
    @ArchTest
    val exceptionHandlersAreWellStructured: ArchRule = classes()
        .that().areAnnotatedWith(ControllerAdvice::class.java)
        .or().areAnnotatedWith(RestControllerAdvice::class.java)
        .should().resideInAPackage("..exception..")
        .andShould().haveSimpleNameEndingWith("Advice")
        .because("Классы перехвата исключений должны находиться в пакете 'exception' и иметь суффикс 'Advice'.")

    /**
     * 8. Запрет внедрения зависимостей через поля (`Field Injection`).
     * Использование аннотации `@Autowired` на полях класса делает код сложным для юнит-тестирования
     * и скрывает зависимости. Требуется внедрение через конструктор (`Constructor Injection`).
     */
    @Suppress("unused")
    @ArchTest
    val noFieldInjection: ArchRule = noFields()
        .should().beAnnotatedWith(Autowired::class.java)
        .because("Используйте внедрение через конструктор (constructor injection) вместо аннотации @Autowired на полях.")

    /**
     * 9. Стандартизация именования DTO-моделей.
     * Объекты передачи данных должны иметь утвержденные суффиксы для лучшего понимания их роли
     * (запросы, ответы, проекции, специализированные структуры данных).
     */
    @Suppress("unused")
    @ArchTest
    val dtosAreWellNamed: ArchRule = classes()
        .that().resideInAPackage("..dto..")
        .and().areNotNestedClasses()
        .should().haveSimpleNameEndingWith("Request")
        .orShould().haveSimpleNameEndingWith("Response")
        .orShould().haveSimpleNameEndingWith("Dto")
        .orShould().haveSimpleNameEndingWith("Info")
        .orShould().haveSimpleNameEndingWith("Status")
        .orShould().haveSimpleNameEndingWith("Projection")
        .orShould().haveSimpleNameEndingWith("Head")
        .orShould().haveSimpleNameEndingWith("Spec")
        .orShould().haveSimpleNameEndingWith("Details")
        .because("Классы в пакете 'dto' должны иметь стандартные суффиксы для запросов, ответов и вложенных данных.")

    /**
     * 10. Стандартизация репозиториев данных.
     * Все интерфейсы и классы для работы с БД в пакете `repository` обязаны заканчиваться на суффикс `Repository`.
     */
    @Suppress("unused")
    @ArchTest
    val repositoriesAreWellNamed: ArchRule = classes()
        .that().resideInAPackage("..repository..")
        .should().haveSimpleNameEndingWith("Repository")
        .because("Все интерфейсы и классы в пакете 'repository' должны заканчиваться на суффикс 'Repository'.")

    /**
     * 11. Обязательный маппинг эндпоинтов в контроллерах.
     * Каждый публичный метод в `@RestController` классе должен иметь аннотацию маппинга
     * (`@GetMapping`, `@PostMapping` и т.д.), чтобы исключить незарегистрированные публичные методы.
     */
    @Suppress("unused")
    @ArchTest
    val controllerMethodsAreMapped: ArchRule = methods()
        .that().arePublic()
        .and().haveNameNotStartingWith("access$")
        .and().areDeclaredInClassesThat().areAnnotatedWith(RestController::class.java)
        .should().beAnnotatedWith(GetMapping::class.java)
        .orShould().beAnnotatedWith(PostMapping::class.java)
        .orShould().beAnnotatedWith(PutMapping::class.java)
        .orShould().beAnnotatedWith(DeleteMapping::class.java)
        .orShould().beAnnotatedWith(PatchMapping::class.java)
        .orShould().beAnnotatedWith(org.springframework.web.bind.annotation.RequestMapping::class.java)
        .because("Все публичные методы в классах @RestController обязаны иметь аннотации маппинга эндпоинтов.")

    /**
     * 12. Контроль транзакций для операций чтения.
     * Публичные методы сервисов, название которых начинается с префиксов чтения (`find`, `get`, `search` и т.д.),
     * обязаны быть помечены аннотацией `@Transactional(readOnly = true)` для оптимизации производительности СУБД Oracle.
     */
    @Suppress("unused")
    @ArchTest
    val readOnlyTransactionsAreUsedForQueries: ArchRule = methods()
        .that().arePublic()
        .and().areDeclaredInClassesThat().areAnnotatedWith(Service::class.java)
        .and().haveNameNotContaining("$")
        .and().haveNameMatching("^(find|get|load|fetch|search|query|list|exists|count).*")
        .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional::class.java)
        .because("Методы чтения данных в сервисах обязаны иметь аннотацию @Transactional(readOnly = true) для оптимизации работы с БД.")

    /**
     * 13. Контроль транзакций для операций записи/модификации.
     * Публичные методы сервисов, выполняющие мутацию данных (сохранение, обновление, удаление),
     * обязаны иметь активную транзакцию (`@Transactional`).
     */
    @Suppress("unused")
    @ArchTest
    val writeTransactionsAreMandatory: ArchRule = methods()
        .that().arePublic()
        .and().areDeclaredInClassesThat().areAnnotatedWith(Service::class.java)
        .and().haveNameNotContaining("$")
        .and().haveNameMatching("^(save|update|delete|create|insert|remove|modify|execute|cancel|register|submit).*")
        .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional::class.java)
        .because("Методы изменения и записи данных в сервисах обязаны иметь аннотацию @Transactional для корректного управления транзакциями БД.")

    /**
     * 14. Предотвращение утечек памяти: запрет изменяемых ( mutable ) полей в классах конфигураций.
     * Классы с аннотацией `@Configuration` должны быть потокобезопасными и содержать только конфигурационные бины,
     * а не хранить изменяемое состояние приложения в полях класса.
     */
    @Suppress("unused")
    @ArchTest
    val configurationsShouldNotBeMutable: ArchRule = classes()
        .that().areAnnotatedWith(org.springframework.context.annotation.Configuration::class.java)
        .should().haveOnlyFinalFields()
        .because("Конфигурационные классы Spring должны быть потокобезопасными и содержать только final-поля.")

    /**
     * 15. Стандартизация именования классов конфигураций.
     * Все конфигурационные классы должны заканчиваться на суффикс `Config` или `Configuration`.
     */
    @Suppress("unused")
    @ArchTest
    val configurationsAreWellNamed: ArchRule = classes()
        .that().areAnnotatedWith(org.springframework.context.annotation.Configuration::class.java)
        .should().haveSimpleNameEndingWith("Config")
        .orShould().haveSimpleNameEndingWith("Configuration")
        .because("Классы конфигураций должны иметь суффикс 'Config' или 'Configuration'.")
}