package com.example.oracleapi

import com.example.oracleapi.log.LogCat
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OracleApiApplication

fun main(args: Array<String>) {
    LogCat("Start Oracle API Service ARS")
    runApplication<OracleApiApplication>(*args)
}
