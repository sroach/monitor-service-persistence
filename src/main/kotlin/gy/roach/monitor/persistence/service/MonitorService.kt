package gy.roach.monitor.persistence.service

import gy.roach.monitor.persistence.entity.MonitorRecord
import gy.roach.monitor.persistence.repository.MonitorRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class MonitorService(private val repository: MonitorRecordRepository) {

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

    @Transactional
    fun deleteRecord(id: Long) {
        repository.deleteById(id)
    }
}
