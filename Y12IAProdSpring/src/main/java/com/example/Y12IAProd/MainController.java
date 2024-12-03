package com.example.Y12IAProd;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;


@Controller
public class MainController {
    
    @RequestMapping("/")
    public String index() {
        return "html/index.html"; // Redirect to static index.html
    }

    @GetMapping("/profile")
    public String profile() {
        return "html/profile.html"; // about.html in static folder
    }

    @GetMapping("/contact")
    public String contact() {
        return "html/contact.html"; // contact.html in static folder
    }

    @GetMapping("/nav")
    public String nav() {
        return "html/nav.html"; // contact.html in static folder
    }

    @GetMapping("/pass")
    public String pass() {
        return "html/pass.html"; // contact.html in static folder
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
        
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "html/error-404.html";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "html/error-500.html";
            }
        }
        return "html/error.html";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String loginFormSubmission() {
        return "redirect:/html/error-404.html";
    }
}