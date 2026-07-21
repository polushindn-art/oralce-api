package com.example.oracleapi.dto.max

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Результат проверки возраста через Цифровой ID")
data class AgeVerificationResponse(
    @field:Schema(description = "Подтверждение возраста 18+", example = "true")
    val isAdult: Boolean,

    @field:Schema(description = "Статус студента")
    val studentStatus: StudentStatus? = null,

    @field:Schema(description = "Статус многодетной семьи")
    val largeFamilyStatus: LargeFamilyStatus? = null,

    @field:Schema(description = "Статус пенсионера", example = "false")
    val pensionerStatus: Boolean? = null,

    @field:Schema(description = "Статус инвалида")
    val disabledPersonStatus: DisabledPersonStatus? = null,

    @field:Schema(description = "Текст ошибки (если есть)", example = "Сессия не найдена")
    val error: String? = null
)

@Schema(description = "Статус студента")
data class StudentStatus(
    @field:Schema(description = "Является ли студентом", example = "true")
    val status: Boolean,

    @field:Schema(description = "Форма обучения", example = "Заочная")
    val educationForm: String?,

    @field:Schema(description = "Название учебного заведения", example = "Московский Государственный Университет")
    val organizationName: String?
)

@Schema(description = "Статус многодетной семьи")
data class LargeFamilyStatus(
    @field:Schema(description = "Есть ли статус многодетной семьи", example = "true")
    val status: Boolean,

    @field:Schema(description = "Регион выдачи документа", example = "г. Москва")
    val district: String?
)

@Schema(description = "Статус инвалида")
data class DisabledPersonStatus(
    @field:Schema(description = "Является ли инвалидом", example = "true")
    val status: Boolean,

    @field:Schema(description = "Группа инвалидности", example = "II группа")
    val disabilityGroup: String?
)
