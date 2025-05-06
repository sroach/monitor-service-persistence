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

    @Query("SELECT * FROM MONITOR_RECORDS WHERE TIMESTAMP < :beforeTime")
    fun findByTimestampBefore(beforeTime: LocalDateTime): List<MonitorRecord>

    @Query("DELETE FROM MONITOR_RECORDS WHERE TIMESTAMP < :beforeTime")
    fun deleteByTimestampBefore(beforeTime: LocalDateTime): Int

    @Query("""
        SELECT mr.* FROM MONITOR_RECORDS mr
        INNER JOIN (
            SELECT NAME, URL, MAX(TIMESTAMP) as MAX_TIMESTAMP
            FROM MONITOR_RECORDS
            GROUP BY NAME, URL
        ) latest ON mr.NAME = latest.NAME AND mr.URL = latest.URL AND mr.TIMESTAMP = latest.MAX_TIMESTAMP
    """)
    fun findUniqueByNameAndUrl(): List<MonitorRecord>

    @Query("SELECT * FROM MONITOR_RECORDS WHERE NAME= :name AND TIMESTAMP >= :fromTime ORDER BY TIMESTAMP DESC")
    fun findByNameAndTimestampAfter(name: String, fromTime: String): List<MonitorRecord>

}
