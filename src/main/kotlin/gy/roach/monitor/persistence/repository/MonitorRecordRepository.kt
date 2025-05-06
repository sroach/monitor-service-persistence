package gy.roach.monitor.persistence.repository

import gy.roach.monitor.persistence.entity.MonitorRecord
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MonitorRecordRepository : CrudRepository<MonitorRecord, Long> {

    fun findByName(name: String): List<MonitorRecord>

    @Query("SELECT * FROM MONITOR_RECORDS WHERE NAME = :name AND URL = :url")
    fun findByNameAndUrl(name: String, url: String): List<MonitorRecord>

    @Query("SELECT * FROM MONITOR_RECORDS WHERE TIMESTAMP >= :fromTime ORDER BY TIMESTAMP DESC")
    fun findByTimestampAfter(fromTime: LocalDateTime): List<MonitorRecord>
}
