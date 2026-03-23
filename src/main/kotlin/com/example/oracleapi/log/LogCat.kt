package com.example.oracleapi.log

import org.slf4j.LoggerFactory
import org.slf4j.Logger

class LogCat {

    companion object {
        const val EVENT = "event happened"
        const val EMPTY = "mpty message"
        const val LOGNAME = "LogCat"
        private val log = LoggerFactory.getLogger(LogCat::class.java)
    }

    constructor() {
        LogCat(EVENT)
    }

    constructor(any: Any) {
        val lineBreak = any.toString().contains('\n') || any.toString().contains('\r')
        val symbol = if (lineBreak) {"↓\n"} else {"→ "}
        val className = any::class.java.simpleName
        show("$LOGNAME[$className] $symbol$any")
    }

    fun show(text: String) {
        //log.info(text)
        println(text)
    }
}