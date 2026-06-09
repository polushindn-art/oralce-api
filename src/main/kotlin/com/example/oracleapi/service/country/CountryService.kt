package com.example.oracleapi.service.country

import com.example.oracleapi.repository.country.CountryRepository
import org.springframework.stereotype.Service

@Service
class CountryService(
    val countryRepository: CountryRepository
) {
    fun existsByRn(rn: Long): Boolean {
        return countryRepository.existsByRn(rn)
    }
}