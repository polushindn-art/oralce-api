package com.example.oracleapi.dto.agn

import com.example.oracleapi.dto.agnphonenumberlist.PhoneListAgn

/**
 * DTO ответ информации о контрагенте
 * */
data class AgnListInfoResponse(
    /**Идентификатор*/
    val rn: Long?,
    /**Наименование*/
    val agnName: String?,
    /**Мнемокод*/
    val agnCode: String?,
    /**Телефонные номера*/
    val phone: List<PhoneListAgn>? = null,
    /**ИНН*/
    val inn: String?,
    /**КПП*/
    val kpp: String?,
    /**Тип контрагента*/
    val agnType: Long?,
    /**Тип контрагента наименование*/
    val agnTypeName: String?,
    /**Адрес факт*/
    val addressFact: String?,
    /**Адрес юр*/
    val addressUl: String?,
    /**eMail*/
    val eMail: String?,
)
