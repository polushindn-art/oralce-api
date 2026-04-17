package com.example.oracleapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class OracleApiApplication

fun main(args: Array<String>) {
    runApplication<OracleApiApplication>(*args)
}
