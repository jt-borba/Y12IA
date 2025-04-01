package com.example.Y12IAProd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * AttendanceScheduler automatically rejects any pending attendance submissions at 3:50 PM daily.
 */
@Component
public class AttendanceScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Runs daily at 3:50 PM. Updates all attendance records with status 'submitted' to 'rejected'.
     * Cron expression: second, minute, hour, day of month, month, day of week.
     */
    @Scheduled(cron = "0 50 15 * * ?")
    public void autoRejectSubmissions() {
        String sql = "UPDATE Attendance SET status='rejected' WHERE status='submitted'";
        int updated = jdbcTemplate.update(sql);
        System.out.println("Auto-rejected " + updated + " attendance submission(s).");
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoClearAttendance() {
        String sql = "DELETE FROM Attendance";
        int rowsDeleted = jdbcTemplate.update(sql);
        System.out.println("Auto-cleared Attendance table. Deleted " + rowsDeleted + " row(s).");
    }    
}
