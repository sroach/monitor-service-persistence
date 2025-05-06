package gy.roach.monitor.persistence.config

import gy.roach.monitor.persistence.service.MonitorService
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import java.io.File

@Configuration
class DataInitializer {
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    @Bean
    fun initDatabase(monitorService: MonitorService, jdbcTemplate: JdbcTemplate): CommandLineRunner {
        return CommandLineRunner {
            // Create data directory if it doesn't exist
            val dataDir = File("./data")
            if (!dataDir.exists()) {
                dataDir.mkdir()
                logger.info("Created data directory at ${dataDir.absolutePath}")
            }

            // Ensure the table is created before any records are inserted
            jdbcTemplate.execute(
                """
                CREATE TABLE IF NOT EXISTS MONITOR_RECORDS (
                    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                    NAME VARCHAR(255) NOT NULL,
                    URL VARCHAR(1024) NOT NULL,
                    STATUS_CODE INT NOT NULL,
                    TIMESTAMP TIMESTAMP NOT NULL,
                    RESPONSE_TIME_MS BIGINT NOT NULL,
                    ERROR_MESSAGE VARCHAR(1024)
                )
                """
            )

            // Check if we already have records (to avoid duplicate initialization)
            val existingRecords = monitorService.getAllRecords()
            if (existingRecords.isEmpty()) {
                logger.info("Initializing database with sample data")

                // Add some sample records
                monitorService.saveRecord(
                    name = "Google",
                    url = "https://www.google.com",
                    statusCode = 200,
                    responseTimeMs = 150,
                    errorMessage = null
                )

                monitorService.saveRecord(
                    name = "GitHub",
                    url = "https://github.com",
                    statusCode = 200,
                    responseTimeMs = 250,
                    errorMessage = null
                )

                monitorService.saveRecord(
                    name = "Example Error",
                    url = "https://nonexistent-site.example",
                    statusCode = 404,
                    responseTimeMs = 100,
                    errorMessage = "Not Found"
                )

                logger.info("Sample data initialization complete")
            } else {
                logger.info("Database already contains ${existingRecords.size} records, skipping initialization")
            }
        }
    }
}
