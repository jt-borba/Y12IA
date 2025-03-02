package com.example.Y12IAProd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private APIController apiController;

    @RequestMapping("/")
    public String index() {
        return "html/index.html";
    }

    // Other mappings ...

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, Object> loginFormSubmission(@RequestParam Map<String, String> values) {
        APIController.ValidationResult result = apiController.validateCredentials(values);
        Map<String, Object> response = new HashMap<>();
        if (result.isValid) {
            response.put("valid", true);
            response.put("year", result.yearValue);
        } else {
            response.put("valid", false);
            response.put("error", "Invalid credentials");
        }
        return response;
    }
}
