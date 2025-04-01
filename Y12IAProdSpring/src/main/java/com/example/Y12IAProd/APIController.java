package com.example.Y12IAProd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@Component
public class APIController {

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    public ValidationResult validateCredentials(Map<String, String> values) {
        String email = values.get("email");
        String password = values.get("password");

        System.out.println("Debug: Received email = " + email);
        System.out.println("Debug: Received password = " + password);

        String url = "jdbc:mysql://localhost:3306/loginDB";
        String user = "root";

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            System.out.println("Debug: Connected to database at " + conn.getMetaData().getURL());
            String sql = "SELECT year FROM Users WHERE LOWER(TRIM(email)) = LOWER(TRIM(?)) AND id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String trimmedEmail = email.trim();
                pstmt.setString(1, trimmedEmail);
                int idValue = Integer.parseInt(password.trim());
                pstmt.setInt(2, idValue);
                System.out.println("Debug: Executing query with email = '" + trimmedEmail + "' and id = " + idValue);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int yearValue = rs.getInt("year");
                        System.out.println("Debug: Authentication successful, year = " + yearValue);
                        return new ValidationResult(true, yearValue);
                    } else {
                        System.out.println("Debug: No matching record found for email = '" + trimmedEmail + "', id = " + idValue);
                        return new ValidationResult(false, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ValidationResult(false, null);
        }
    }

    public static class ValidationResult {
        public final boolean isValid;
        public final Integer yearValue;

        public ValidationResult(boolean isValid, Integer yearValue) {
            this.isValid = isValid;
            this.yearValue = yearValue;
        }
    }
}
