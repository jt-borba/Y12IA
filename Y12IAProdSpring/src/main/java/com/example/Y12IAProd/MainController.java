package com.example.Y12IAProd;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {
    
    @RequestMapping("/")
    public String index() {
        return "html/index.html"; // Redirect to static index.html
    }

    @GetMapping("/profile")
    public String profile() {
        return "html/profile.html"; // about.html in templates folder
    }

    @GetMapping("/contact")
    public String contact() {
        return "html/contact.html"; // contact.html in templates folder
    }

    @GetMapping("/nav")
    public String nav() {
        return "html/nav.html"; // contact.html in templates folder
    }

    @GetMapping("/pass")
    public String pass() {
        return "html/pass.html"; // contact.html in templates folder
    }
}