package com.example.Y12IAProd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private APIController apiController;

    // Database connection details for querying attendance status
    private final String dbUrl = "jdbc:mysql://localhost:3306/loginDB";
    private final String dbUser = "root";

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    // Main page (attendance reporting page)
    @RequestMapping("/")
    public String index() {
        return "html/index.html";
    }

    // Mapping for login submission
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, Object> loginFormSubmission(@RequestParam Map<String, String> values, HttpSession session) {
        APIController.ValidationResult result = apiController.validateCredentials(values);
        Map<String, Object> response = new HashMap<>();
        if (result.isValid) {
            response.put("valid", true);
            response.put("year", result.yearValue);
            // Save something to remember user for future use
            session.setAttribute("userId", Integer.parseInt(values.get("password").trim()));
        } else {
            response.put("valid", false);
            response.put("error", "Invalid credentials");
        }
        return response;
    }
    
    // Mapping for the Pass page
    @RequestMapping("/pass")
    public String passPage() {
        return "html/pass.html";
    }
    
    // Mapping for the Contact page
    @RequestMapping("/contact")
    public String contactPage() {
        return "html/contact.html";
    }
    
    // Mapping for the Profile page
    @RequestMapping("/profile")
    public String profilePage() {
        return "html/profile.html";
    }
    
    // New endpoint: Returns the latest attendance status for the logged-in user.
    @RequestMapping(path = "/getAttendanceStatus", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, Object> getAttendanceStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.put("status", null);
            response.put("error", "User not logged in");
            return response;
        }
        // Query the Attendance table for the latest record for this user
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "SELECT status, date, time FROM Attendance WHERE user_id = ? ORDER BY submission_timestamp DESC LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        response.put("status", rs.getString("status"));
                        response.put("date", rs.getString("date"));
                        response.put("time", rs.getString("time"));
                    } else {
                        response.put("status", null);
                        response.put("error", "No attendance record found");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Exception while fetching attendance status");
        }
        return response;
    }
}
