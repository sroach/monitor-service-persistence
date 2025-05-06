package gy.roach.monitor.persistence.entity

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("MONITOR_RECORDS")
@Schema(description = "Entity representing a monitor record")
data class MonitorRecord(
    @Id
    @field:Schema(description = "Unique identifier of the record", example = "1")
    val id: Long? = null,

    @Column("NAME")
    @field:Schema(description = "Name of the monitor", example = "Google")
    val name: String,

    @Column("URL")
    @field:Schema(description = "URL being monitored", example = "https://www.google.com")
    val url: String,

    @Column("STATUS_CODE")
    @field:Schema(description = "HTTP status code of the response", example = "200")
    val statusCode: Int,

    @Column("TIMESTAMP")
    @field:Schema(description = "Timestamp when the record was created", example = "2023-06-15T10:15:30")
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column("RESPONSE_TIME_MS")
    @field:Schema(description = "Response time in milliseconds", example = "150")
    val responseTimeMs: Long,

    @Column("ERROR_MESSAGE")
    @field:Schema(description = "Error message if any", example = "Connection timeout", nullable = true)
    val errorMessage: String? = null
)
