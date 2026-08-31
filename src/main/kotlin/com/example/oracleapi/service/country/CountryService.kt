package com.example.oracleapi.service.country

import com.example.oracleapi.repository.country.CountryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CountryService(
    private val countryRepository: CountryRepository
) {
    @Transactional(readOnly = true)
    fun existsByRn(rn: Long): Boolean {
        return countryRepository.existsByRn(rn)
    }
}