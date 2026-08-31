package com.example.oracleapi.service.admin

import com.zaxxer.hikari.HikariDataSource
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class DatabaseAdminService(
    private val dataSource: DataSource
) {

    /**
     * Мягкий сброс соединений пула HikariCP.
     * Закрывает свободные коннекты, а занятые помещает в очередь на удаление
     * сразу после завершения их текущих транзакций.
     */
    fun softEvictPoolConnections() {
        if (dataSource is HikariDataSource) {
            val poolMXBean = dataSource.hikariPoolMXBean
            poolMXBean?.softEvictConnections()
        } else {
            throw IllegalStateException("Текущий DataSource не поддерживает управление HikariCP")
        }
    }
}