package gy.roach.monitor.persistence.scheduler

import gy.roach.monitor.persistence.service.MonitorService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Scheduler for cleaning up old monitor records to keep the database size manageable.
 */
@Component
class DataCleanupScheduler(private val monitorService: MonitorService) {
    private val logger = LoggerFactory.getLogger(DataCleanupScheduler::class.java)
    
    @Value("\${monitor.data.retention.days:7}")
    private val dataRetentionDays: Long = 7
    
    /**
     * Scheduled task that runs daily to delete records older than the configured retention period.
     * Default is to keep one week of data.
     */
    @Scheduled(cron = "\${monitor.data.cleanup.cron:0 0 0 * * ?}") // Default: Run at midnight every day
    fun cleanupOldData() {
        logger.info("Starting scheduled cleanup of old monitor records")
        logger.info("Keeping data for the last $dataRetentionDays days")
        
        val deletedCount = monitorService.deleteRecordsOlderThan(dataRetentionDays)
        
        logger.info("Scheduled cleanup completed. Deleted $deletedCount old records")
    }
}