package com.example.Y12IAProd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private APIController apiController;

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
            // Set session attribute if needed
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
}
