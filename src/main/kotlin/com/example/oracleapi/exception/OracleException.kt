package com.example.oracleapi.exception

data class OracleError(
    val code: Int,
    val message: String
)

class OracleException(
    val oracleCode: Int,
    override val message: String,
    val sqlState: String? = null,
    val details: String? = null,
    val businessErrors: List<OracleError>? = null,   // только ORA-20000
    val technicalErrors: List<OracleError>? = null,  // остальные (строки кода, ORA-06512 и т.д.)
    val nestedErrors: List<OracleError>? = null      // все ошибки вместе
) : RuntimeException(message) {

    companion object {
        fun business(
            message: String,
            businessErrors: List<OracleError>? = null,
            technicalErrors: List<OracleError>? = null,
            nestedErrors: List<OracleError>? = null
        ): OracleException {
            return OracleException(
                oracleCode = 20000,
                message = message,
                businessErrors = businessErrors,
                technicalErrors = technicalErrors,
                nestedErrors = nestedErrors
            )
        }
    }
}