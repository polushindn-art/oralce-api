package com.example.oracleapi.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.getProperty
import java.awt.EventQueue
import java.awt.GraphicsEnvironment
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicReference
import javax.sql.DataSource
import javax.swing.*
import kotlin.system.exitProcess

@Configuration
class OracleConfig(private val env: Environment) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val passwordHolder = AtomicReference<String>()

    @Bean
    fun dataSource(): DataSource {
        val password = getPassword()

        validateConnection(password)

        logger.info("Creating DataSource for Oracle database")
        val jdbcUrl = env.getRequiredProperty("spring.datasource.url")
        val username = env.getRequiredProperty("spring.datasource.username")

        return HikariDataSource(HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.driverClassName = env.getProperty("spring.datasource.driver-class-name", "oracle.jdbc.OracleDriver")

            this.maximumPoolSize = env.getProperty<Int>("spring.datasource.hikari.maximum-pool-size") ?: 20
            this.minimumIdle = env.getProperty<Int>("spring.datasource.hikari.minimum-idle") ?: 10
            this.connectionTimeout = env.getProperty<Long>("spring.datasource.hikari.connection-timeout") ?: 30000
            this.idleTimeout = env.getProperty<Long>("spring.datasource.hikari.idle-timeout") ?: 600000
            this.maxLifetime = env.getProperty<Long>("spring.datasource.hikari.max-lifetime") ?: 1800000

            this.connectionTestQuery = "SELECT 1 FROM DUAL"
            this.poolName = "OracleArsPool"
            this.initializationFailTimeout = -1
        })
    }

    private fun validateConnection(password: String) {
        val url = env.getRequiredProperty("spring.datasource.url")
        val username = env.getRequiredProperty("spring.datasource.username")

        logger.info("Validating connection to Oracle database...")

        try {
            Class.forName(env.getProperty("spring.datasource.driver-class-name", "oracle.jdbc.OracleDriver"))
            DriverManager.getConnection(url, username, password).use { conn ->
                conn.createStatement().use { stmt ->
                    val sql = "SELECT 1 FROM DUAL"
                    stmt.executeQuery(sql).use { rs ->
                        if (rs.next()) {
                            logger.info("✅ Connection to Oracle validated successfully")
                        } else {
                            throw SQLException("Failed test query execution")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("❌ Database connection error: ${e.message}")

            when {
                e.message?.contains("ORA-01017") == true -> {
                    showFatalError(
                        "AUTHENTICATION ERROR",
                        "Invalid credentials for user '$username'",
                        "Check your password and restart the application"
                    )
                }
                e.message?.contains("ORA-12154") == true -> {
                    showFatalError(
                        "CONNECTION ERROR",
                        "Incorrect SID or host",
                        "Check URL: $url"
                    )
                }
                e.message?.contains("Network adapter") == true -> {
                    showFatalError(
                        "NETWORK ERROR",
                        "Unable to connect to the database server",
                        "Verify server availability at qrlw.ars:1521"
                    )
                }
                else -> {
                    showFatalError(
                        "UNKNOWN ERROR",
                        e.message ?: "Unknown error occurred",
                        "Review logs for details"
                    )
                }
            }

            exitProcess(1)
        }
    }

    private fun showFatalError(title: String, message: String, suggestion: String) {
        val fullMessage = """
            
            ╔═══════════════════════════════════════════════════════════╗
            ║  ⛓️ $title
            ╠═══════════════════════════════════════════════════════════╣
            ║  $message
            ║  
            ║  💡 $suggestion
            ╚═══════════════════════════════════════════════════════════╝           
        """.trimIndent()

        logger.error(fullMessage)

        if (!GraphicsEnvironment.isHeadless()) {
            try {
                SwingUtilities.invokeLater {
                    JOptionPane.showMessageDialog(
                        null,
                        "$message\n\n💡 $suggestion",
                        "⛓️ $title",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
                Thread.sleep(2000)
            } catch (_: Exception) {
                // Ignore display errors
            }
        }

        System.err.println(fullMessage)
    }

    private fun getPassword(): String {
        // 1. ПРОВЕРЯЕМ ПЕРЕМЕННЫЕ ОКРУЖЕНИЯ (для Docker)
        env.getProperty("spring.datasource.password")?.let {
            if (it.isNotBlank()) {
                logger.info("✅ Using password from environment variable")
                return it
            }
        }

        // 2. Проверяем, не в Docker ли мы (чтобы дать понятную ошибку)
        if (isRunningInDocker()) {
            val errorMsg = """
            ❌ DATABASE PASSWORD NOT FOUND IN DOCKER ENVIRONMENT
            
            Please set the password using:
            -e SPRING_DATASOURCE_PASSWORD=your_password
            
            Example:
            docker run -e SPRING_DATASOURCE_PASSWORD=htrhtfwbz -p 8080:8080 oracle-api
        """.trimIndent()

            logger.error(errorMsg)
            throw IllegalStateException("SPRING_DATASOURCE_PASSWORD environment variable is required in Docker")
        }

        // 3. Для тестового окружения
        if (isTestEnvironment()) {
            logger.warn("TEST ENVIRONMENT: Using test password")
            return "test_password"
        }

        // 4. Вне Docker - запрашиваем пароль интерактивно
        passwordHolder.get()?.let { return it }
        return requestPassword()
    }

    private fun requestPassword(): String {
        return try {
            if (GraphicsEnvironment.isHeadless()) {
                requestPasswordFromConsole()
            } else {
                requestPasswordFromDialog()
            }
        } catch (e: Exception) {
            logger.error("Error requesting password", e)
            requestPasswordFromConsole()
        }
    }

    private fun isRunningInDocker(): Boolean {
        return File("/.dockerenv").exists() ||
                System.getenv("DOCKER_CONTAINER") != null ||
                System.getenv("RUNNING_IN_DOCKER") != null
    }

    private fun requestPasswordFromDialog(): String {
        return (if (EventQueue.isDispatchThread()) {
            showPasswordDialog()
        } else {
            SwingUtilities.invokeAndWait { showPasswordDialog() }
        }) as String
    }

    private fun showPasswordDialog(): String {
        val passwordField = JPasswordField(20)
        val panel = JPanel().apply {
            add(JLabel("Oracle Password:"))
            add(passwordField)
        }

        val result = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Authentication",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        )

        if (result != JOptionPane.OK_OPTION) {
            logger.error("User canceled password input")
            exitProcess(1)
        }

        val password = String(passwordField.password)
        if (password.isBlank()) {
            JOptionPane.showMessageDialog(
                null,
                "The password cannot be empty",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
            return showPasswordDialog()
        }

        passwordHolder.set(password)
        return password
    }

    private fun requestPasswordFromConsole(): String {
        println("\n" + "=".repeat(50))
        println("ENTER PASSWORD FOR ORACLE")
        println("=".repeat(50))

        val console = System.console()
        val password = if (console != null) {
            String(console.readPassword("Password: "))
        } else {
            print("Password (not hidden): ")
            readlnOrNull() ?: ""
        }

        if (password.isBlank()) {
            println("The password cannot be empty")
            return requestPasswordFromConsole()
        }

        passwordHolder.set(password)
        return password
    }

    private fun isTestEnvironment(): Boolean {
        return env.activeProfiles.any { it.contains("test") } ||
                env.getProperty("spring.profiles.active") == "test" ||
                System.getProperty("spring.profiles.active") == "test"
    }
}