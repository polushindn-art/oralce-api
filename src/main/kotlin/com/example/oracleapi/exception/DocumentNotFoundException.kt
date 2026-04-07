package com.example.oracleapi.exception

class DocumentNotFoundException(rn: Long) : RuntimeException("Документ с RN=$rn не найден")