package gy.roach.monitor.persistence.service

import gy.roach.monitor.persistence.entity.MonitorRecord
import gy.roach.monitor.persistence.repository.MonitorRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import org.slf4j.LoggerFactory

@Service
class MonitorService(private val repository: MonitorRecordRepository) {
    private val logger = LoggerFactory.getLogger(MonitorService::class.java)

    @Transactional
    fun saveRecord(name: String, url: String, statusCode: Int, responseTimeMs: Long, errorMessage: String? = null): MonitorRecord {
        val record = MonitorRecord(
            name = name,
            url = url,
            statusCode = statusCode,
            timestamp = LocalDateTime.now(),
            responseTimeMs = responseTimeMs,
            errorMessage = errorMessage
        )
        return repository.save(record)
    }

    @Transactional(readOnly = true)
    fun getAllRecords(): List<MonitorRecord> {
        return repository.findAll().toList()
    }

    @Transactional(readOnly = true)
    fun getRecordsByName(name: String): List<MonitorRecord> {
        return repository.findByName(name)
    }

    @Transactional(readOnly = true)
    fun getRecordById(id: Long): MonitorRecord? {
        return repository.findById(id).orElse(null)
    }

    @Transactional(readOnly = true)
    fun getRecordsFromLastSeconds(seconds: Long): List<MonitorRecord> {
        val fromTime = LocalDateTime.now().minus(seconds, ChronoUnit.SECONDS)
        return repository.findByTimestampAfter(fromTime)
    }

    @Transactional(readOnly = true)
    fun getUniqueRecordsByNameAndUrl(): List<MonitorRecord> {
        return repository.findUniqueByNameAndUrl()
    }

    @Transactional(readOnly = true)
    fun getRecordsSinceTimestamp(name: String, timestamp: String): List<MonitorRecord> {
        return repository.findByNameAndTimestampAfter(name, timestamp)
    }

    @Transactional
    fun deleteRecord(id: Long) {
        repository.deleteById(id)
    }

    /**
     * Deletes records older than the specified number of days.
     * @param days The number of days to keep records for
     * @return The number of records deleted
     */
    @Transactional
    fun deleteRecordsOlderThan(days: Long): Int {
        val cutoffDate = LocalDateTime.now().minus(days, ChronoUnit.DAYS)
        logger.info("Deleting records older than: $cutoffDate")
        val deletedCount = repository.deleteByTimestampBefore(cutoffDate)
        logger.info("Deleted $deletedCount old records")
        return deletedCount
    }
}
