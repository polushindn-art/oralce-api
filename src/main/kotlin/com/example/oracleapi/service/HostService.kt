package com.example.oracleapi.service

import com.example.oracleapi.Helper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Component
class HostService(
    @param:Value("\${spring.datasource.url:}") private val dataSourceUrl: String
) {
    fun getHost(): Triple<String, String, String> {
        return Helper.parseOracleJdbcUrl(dataSourceUrl)
    }
}