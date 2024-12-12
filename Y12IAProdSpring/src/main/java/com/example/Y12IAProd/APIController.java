package com.example.Y12IAProd;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@RestController
public class APIController {
    public static ValidationResult validateCredentials(Map<String, String> multivalueMap) {
        String email = multivalueMap.get("email");
        String password = multivalueMap.get("password");

        String url = "jdbc:mysql://localhost:3306/loginDB";
        String user = "root";
        String pass = System.getenv("DB_PASSWORD");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT year FROM Users WHERE email = ? AND ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                int passwordAsInt = Integer.parseInt(password);
                pstmt.setInt(2, passwordAsInt);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int yearValue = rs.getInt("year");
                        return new ValidationResult(true, yearValue);
                    } else {
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
