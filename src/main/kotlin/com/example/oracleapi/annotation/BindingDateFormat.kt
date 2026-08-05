package com.example.oracleapi.annotation

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside
import com.fasterxml.jackson.annotation.JsonFormat

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@JacksonAnnotationsInside
@JsonFormat(
    shape = JsonFormat.Shape.STRING,
    pattern = "dd.MM.yyyy"
)
annotation class BindingDateFormat
