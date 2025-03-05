package com.example.Y12IAProd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;


/**
 * AttendanceController handles attendance submissions and parent responses.
 */
@Controller
public class AttendanceController {

    @Autowired
    private JavaMailSender mailSender;

    // Database connection details – consider externalizing these in production.
    private final String dbUrl = "jdbc:mysql://localhost:3306/loginDB";
    private final String dbUser = "root";

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    /**
     * Receives attendance submission requests, inserts a record, and emails the parent.
     *
     * @param date The selected date (format: yyyy-MM-dd).
     * @param time The selected time (format: HH:mm).
     * @param session HttpSession holding the logged-in user's info.
     * @return JSON response indicating success or failure.
     */
    @PostMapping("/submitAttendance")
@ResponseBody
public Map<String, Object> submitAttendance(@RequestParam String date,
                                            @RequestParam String time,
                                            HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    try {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("Debug: User not logged in; session userId is null.");
            response.put("success", false);
            response.put("error", "User not logged in");
            return response;
        }
        System.out.println("Debug: Submitting attendance for userId: " + userId +
                           " with date: " + date + " and time: " + time);
        
        // Convert date from "Mar 06, 2025" to "2025-03-06"
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = LocalDate.parse(date, inputFormatter).format(outputFormatter);
        System.out.println("Debug: Formatted date: " + formattedDate);

        // Insert a new attendance record with status 'submitted'
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Debug: Connected to database for attendance submission.");
            String sql = "INSERT INTO Attendance (user_id, date, time, status) VALUES (?, ?, ?, 'submitted')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, formattedDate); // use formatted date
                pstmt.setString(3, time);
                int rowsInserted = pstmt.executeUpdate();
                System.out.println("Debug: Inserted " + rowsInserted + " row(s) into Attendance.");
            }
        } catch (Exception dbEx) {
            System.out.println("Debug: Exception during database insertion.");
            dbEx.printStackTrace();
            response.put("success", false);
            response.put("error", "Database insertion error");
            return response;
        }

        // Retrieve parent's email for the given user (stub implementation)
        String parentEmail = getParentEmailForUser(userId);
        System.out.println("Debug: Retrieved parent's email: " + parentEmail + " for userId: " + userId);

        if (parentEmail != null) {
            String emailSubject = "Attendance Report Submitted";
            String emailBody = "Hello Parent,\n\n" +
                    "A new attendance report has been submitted for date " + formattedDate + " at " + time + ".\n" +
                    "To approve, click: http://yourserver/attendance/approve?userId=" + userId + "\n" +
                    "To reject, click: http://yourserver/attendance/reject?userId=" + userId + "\n\n";
            System.out.println("Debug: Sending email to parent.");
            sendEmail(parentEmail, emailSubject, emailBody);
            System.out.println("Debug: Email sent successfully.");
        } else {
            System.out.println("Debug: No parent's email found for userId: " + userId);
        }

        response.put("success", true);
    } catch (Exception e) {
        System.out.println("Debug: Exception in submitAttendance method:");
        e.printStackTrace();
        response.put("success", false);
        response.put("error", "Exception in submitAttendance");
    }
    return response;
}

    /**
     * Handles approval via magic link.
     *
     * @param userId The ID of the user whose attendance report is approved.
     * @return A simple message indicating the outcome.
     */
    @GetMapping("/attendance/approve")
    @ResponseBody
    public String approveAttendance(@RequestParam int userId) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "UPDATE Attendance SET status='approved' WHERE user_id=? AND status='submitted'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                int updated = pstmt.executeUpdate();
                return (updated > 0) ? "Attendance approved." : "No submitted attendance found or already processed.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing approval.";
        }
    }

    /**
     * Handles rejection via magic link.
     *
     * @param userId The ID of the user whose attendance report is rejected.
     * @return A simple message indicating the outcome.
     */
    @GetMapping("/attendance/reject")
    @ResponseBody
    public String rejectAttendance(@RequestParam int userId) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "UPDATE Attendance SET status='rejected' WHERE user_id=? AND status='submitted'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                int updated = pstmt.executeUpdate();
                return (updated > 0) ? "Attendance rejected." : "No submitted attendance found or already processed.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing rejection.";
        }
    }

    /**
     * Stub for retrieving a parent's email for the user.
     * Replace with an actual database query against your Parents table.
     *
     * @param userId The user's ID.
     * @return A parent's email address or null if not found.
     */
    private String getParentEmailForUser(int userId) {
        // TODO: Implement actual query to fetch parent's email from the Parents table.
        System.out.println("Debug: getParentEmailForUser called for userId: " + userId);
        return "parent@example.com";
    }

    /**
     * Sends an email using the configured JavaMailSender.
     *
     * @param to Recipient's email address.
     * @param subject Email subject.
     * @param text Email body.
     */
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        System.out.println("Debug: sendEmail executed for recipient: " + to);
    }
}
