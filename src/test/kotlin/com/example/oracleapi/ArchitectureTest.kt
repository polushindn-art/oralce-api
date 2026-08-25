package com.example.oracleapi

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packages = ["com.example.oracleapi"])
class ArchitectureTest {

    // 1. Главная защита слоев (Controller -> Service -> Repository)
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

    // 2. Контроллеры должны заканчиваться на Controller
    @Suppress("unused")
    @ArchTest
    val controllersShouldBeSuffixed: ArchRule = classes()
        .that().areAnnotatedWith(RestController::class.java)
        .should().haveSimpleNameEndingWith("Controller")
        .because("Все классы с аннотацией @RestController обязаны заканчиваться на суффикс 'Controller'.")

    // 3. Сервисы должны заканчиваться на Service
    @Suppress("unused")
    @ArchTest
    val servicesShouldBeSuffixed: ArchRule = classes()
        .that().areAnnotatedWith(Service::class.java)
        .should().haveSimpleNameEndingWith("Service")
        .because("Все компоненты с аннотацией @Service обязаны заканчиваться на суффикс 'Service'.")

    // 4. ЗАЩИТА API: Контроллеры не должны знать про сущности БД (Entity)
    @Suppress("unused")
    @ArchTest
    val controllersShouldNotDependOnEntities: ArchRule = noClasses()
        .that().resideInAPackage("..controller..")
        .should().dependOnClassesThat()
        .resideInAPackage("..entity..")
        .because("Контроллеры не имеют права работать с сущностями БД напрямую! Используйте DTO.")
}