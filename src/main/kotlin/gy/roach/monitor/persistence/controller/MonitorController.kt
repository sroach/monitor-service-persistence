package gy.roach.monitor.persistence.controller

import gy.roach.monitor.persistence.entity.MonitorRecord
import gy.roach.monitor.persistence.service.MonitorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/records")
@Tag(name = "Monitor Records", description = "API for managing monitor records")
class MonitorController(private val monitorService: MonitorService) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    @Operation(summary = "Create a new monitor record", description = "Creates a new monitor record with the provided data")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Record created successfully",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))])
    ])
    fun createRecord(@RequestBody request: RecordRequest): ResponseEntity<MonitorRecord> {
        val savedRecord = monitorService.saveRecord(
            name = request.name,
            url = request.url,
            statusCode = request.statusCode,
            responseTimeMs = request.responseTimeMs,
            errorMessage = request.errorMessage
        )
        return ResponseEntity(savedRecord, HttpStatus.CREATED)
    }

    @GetMapping
    @Operation(summary = "Get all monitor records", description = "Retrieves a list of all monitor records")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved list of records",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))])
    ])
    fun getAllRecords(): ResponseEntity<List<MonitorRecord>> {
        return ResponseEntity(monitorService.getAllRecords(), HttpStatus.OK)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a monitor record by ID", description = "Retrieves a monitor record by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved the record",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))]),
        ApiResponse(responseCode = "404", description = "Record not found", content = [Content()])
    ])
    fun getRecordById(
        @Parameter(description = "ID of the record to retrieve", required = true)
        @PathVariable id: Long
    ): ResponseEntity<MonitorRecord> {
        val record = monitorService.getRecordById(id)
        return if (record != null) {
            ResponseEntity(record, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get monitor records by name", description = "Retrieves all monitor records with the specified name")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved records",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))])
    ])
    fun getRecordsByName(
        @Parameter(description = "Name of the records to retrieve", required = true)
        @PathVariable name: String
    ): ResponseEntity<List<MonitorRecord>> {
        return ResponseEntity(monitorService.getRecordsByName(name), HttpStatus.OK)
    }

    @GetMapping("/last/{seconds}")
    @Operation(summary = "Get monitor records from the last n seconds", description = "Retrieves all monitor records from the last n seconds")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved records",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))])
    ])
    fun getRecordsFromLastSeconds(
        @Parameter(description = "Number of seconds to look back", required = true, example = "60")
        @PathVariable seconds: Long
    ): ResponseEntity<List<MonitorRecord>> {
        return ResponseEntity(monitorService.getRecordsFromLastSeconds(seconds), HttpStatus.OK)
    }

    @GetMapping("/unique")
    @Operation(summary = "Get unique monitor records by name and URL", description = "Retrieves a list of monitor records where each name and URL combination appears only once (the most recent record)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved unique records",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))])
    ])
    fun getUniqueRecordsByNameAndUrl(): ResponseEntity<List<MonitorRecord>> {
        return ResponseEntity(monitorService.getUniqueRecordsByNameAndUrl(), HttpStatus.OK)
    }

    @GetMapping("/name/{name}/since/{timestamp}")
    @Operation(summary = "Get monitor records since a specific timestamp", description = "Retrieves all monitor records with timestamps after the specified time")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved records",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = MonitorRecord::class))]),
        ApiResponse(responseCode = "400", description = "Invalid timestamp format", content = [Content()])
    ])
    fun getRecordsSinceTimestamp(
        @Parameter(description = "Name of the records to retrieve", required = true)
        @PathVariable name: String,
        @Parameter(description = "Timestamp in ISO format (e.g., '2023-06-15T10:15:30')", required = true)
        @PathVariable timestamp: String
    ): ResponseEntity<*> {
        return try {
            val dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME)
            ResponseEntity(monitorService.getRecordsSinceTimestamp(name,  dateTime), HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Invalid timestamp format: $timestamp", e)
            ResponseEntity("Invalid timestamp format. Use ISO format (e.g., '2023-06-15T10:15:30')", HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a monitor record", description = "Deletes a monitor record by its ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Record successfully deleted", content = [Content()])
    ])
    fun deleteRecord(
        @Parameter(description = "ID of the record to delete", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        monitorService.deleteRecord(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}

@Schema(description = "Request object for creating a monitor record")
data class RecordRequest(
    @field:Schema(description = "Name of the monitor", example = "Google", required = true)
    val name: String,

    @field:Schema(description = "URL being monitored", example = "https://www.google.com", required = true)
    val url: String,

    @field:Schema(description = "HTTP status code of the response", example = "200", required = true)
    val statusCode: Int,

    @field:Schema(description = "Response time in milliseconds", example = "150", required = true)
    val responseTimeMs: Long,

    @field:Schema(description = "Error message if any", example = "Connection timeout", required = false)
    val errorMessage: String? = null
)
