package com.example.oracleapi.dto.max.common

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ от MAX API")
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaxApiResponse(
    @field:Schema(description = "Статус ответа", example = "success", allowableValues = ["success", "error"])
    val status: String,

    @field:Schema(description = "Данные ответа (при успехе)")
    val data: MaxDataSpec? = null,

    @field:Schema(description = "Ошибка (при ошибке)")
    val error: MaxErrorDetails? = null,

    @field:Schema(description = "Метаданные запроса")
    val metadata: MaxMetadataSpec? = null
)

@Schema(description = "Данные ответа MAX API")
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaxDataSpec(
    @field:Schema(description = "ID сессии")
    @field:JsonProperty("session_id")
    val sessionId: String,

    @field:Schema(description = "Статус верификации")
    @field:JsonProperty("verification_status")
    val verificationStatus: String,

    @field:Schema(description = "Детали верификации")
    @field:JsonProperty("verification_details")
    val verificationDetails: VerificationDetails,

    // В ДОКУМЕНТАЦИИ ЕСТЬ, но в реальных ответах НЕТ → делаем опциональным!
    @field:Schema(description = "Метаданные (внутри data)")
    @field:JsonProperty("metadata")
    val metadata: MaxMetadataSpec? = null
)

@Schema(description = "Детали верификации")
@JsonIgnoreProperties(ignoreUnknown = true)
data class VerificationDetails(
    @field:Schema(description = "Подтверждение возраста")
    val adult: AdultStatusInfo,

    @field:Schema(description = "Статус студента (опционально)")
    val student: StudentStatusFromMaxInfo? = null,

    @field:Schema(description = "Статус многодетной семьи (опционально)")
    @field:JsonProperty("certificateLargeFamily")
    val certificateLargeFamily: LargeFamilyStatusFromMaxInfo? = null,

    @field:Schema(description = "Статус пенсионера (опционально)")
    val pensioner: PensionerStatusInfo? = null,

    @field:Schema(description = "Статус инвалида (опционально)")
    @field:JsonProperty("disabledPerson")
    val disabledPerson: DisabledPersonStatusFromMaxInfo? = null,

    @field:Schema(description = "Сырые данные с подписью Минцифры (опционально)")
    @field:JsonProperty("raw_data")
    val rawData: Map<String, Any>? = null
)

@Schema(description = "Статус возраста")
data class AdultStatusInfo(
    @field:Schema(description = "Подтвержден ли возраст 18+", example = "true")
    val status: Boolean
)

@Schema(description = "Статус студента от MAX")
@JsonIgnoreProperties(ignoreUnknown = true)
data class StudentStatusFromMaxInfo(
    @field:Schema(description = "Является ли студентом", example = "true")
    val status: Boolean,

    @field:Schema(description = "Форма обучения", example = "Заочная")
    @field:JsonProperty("studenttickets_education_form")
    val studentticketsEducationForm: String? = null,

    @field:Schema(description = "Название учебного заведения", example = "Московский Государственный Университет")
    @field:JsonProperty("studenttickets_organization_name")
    val studentticketsOrganizationName: String? = null
)

@Schema(description = "Статус многодетной семьи от MAX")
@JsonIgnoreProperties(ignoreUnknown = true)
data class LargeFamilyStatusFromMaxInfo(
    @field:Schema(description = "Есть ли статус многодетной семьи", example = "true")
    val status: Boolean,

    @field:Schema(description = "Регион выдачи документа", example = "г. Санкт-Петербург")
    val district: String? = null
)

@Schema(description = "Статус пенсионера от MAX")
@JsonIgnoreProperties(ignoreUnknown = true)
data class PensionerStatusInfo(
    @field:Schema(description = "Является ли пенсионером", example = "false")
    val status: Boolean
)

@Schema(description = "Статус инвалида от MAX")
@JsonIgnoreProperties(ignoreUnknown = true)
data class DisabledPersonStatusFromMaxInfo(
    @field:Schema(description = "Является ли инвалидом", example = "true")
    val status: Boolean,

    @field:Schema(description = "Группа инвалидности", example = "II группа")
    @field:JsonProperty("codeGroupDisability")
    val codeGroupDisability: String? = null
)

@Schema(description = "Ошибка от MAX API")
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaxErrorDetails(
    @field:Schema(description = "Код ошибки", example = "SESSION_NOT_FOUND")
    val code: String,

    @field:Schema(description = "Сообщение об ошибке", example = "Сессия верификации не найдена")
    val message: String,

    @field:Schema(description = "Детали ошибки", example = "Указанный идентификатор сессии не существует или устарел")
    val details: String? = null
)

@Schema(description = "Метаданные запроса")
@JsonIgnoreProperties(ignoreUnknown = true)
data class MaxMetadataSpec(
    @field:Schema(description = "ID запроса", example = "req_123456789")
    @field:JsonProperty("request_id")
    val requestId: String,

    @field:Schema(description = "Время запроса", example = "2024-01-15T14:31:00Z")
    val timestamp: String
)