package com.example.oracleapi.view

abstract class ViewCommon {
    companion object {
        const val PBARCODE = "V_PBARCODE_ALL"
    }

    abstract fun execute(): List<Map<String, Any>>
}