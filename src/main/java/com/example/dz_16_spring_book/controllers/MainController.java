package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.UserCookieInfo;
import com.example.dz_16_spring_book.models.Customer;
import com.example.dz_16_spring_book.models.Landlord;
import com.example.dz_16_spring_book.models.User;
import com.example.dz_16_spring_book.repositories.CustomerRepository;
import com.example.dz_16_spring_book.repositories.LandlordRepository;
import com.example.dz_16_spring_book.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class MainController {
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LandlordRepository landlordRepository;

    @GetMapping("/")
    public String getHome(@RequestParam(name = "text", required = false, defaultValue = "MY PAGE")String text,
                          Model model, HttpServletRequest request){
        model.addAttribute("text", text);
        UserCookieInfo info = readCookies(request);
        if(info != null){
            User user = userRepository.findById(info.getId()).orElse(null);
            if(user != null){
                if("customer".equals(info.getRole()) || "admin".equals(info.getRole())){
                    Customer customer = customerRepository.findById(user.getCustomer().getId()).orElse(null);
                    if(customer != null){
                        model.addAttribute("userInfo", customer);
                    }
                }else if("landlord".equals(info.getRole())){
                    Landlord landlord = landlordRepository.findById(user.getLandlord().getId()).orElse(null);
                    if(landlord != null){
                        model.addAttribute("userInfo", landlord);
                    }
                }
            }
        }
        return "home";
    }

    private UserCookieInfo readCookies(HttpServletRequest request){
        Cookie[] cookies = request.getCookies(); // Get all cookies
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("log".equals(cookie.getName())) { // Check for the 'log' cookie
                    UserCookieInfo info = parseCookieValue(cookie.getValue()); // Parse it
                    // Now you have the object with the cookie information
                    // You can use info.getId(), info.getLogin(), and info.getRole() to access the properties
                    logger.info("ID: " + info.getId()  + ", LOGIN: " + info.getLogin() + ", ROLE: " + info.getRole());
                    return info;
                }
            }
        }
        return null;
    }

    private UserCookieInfo parseCookieValue(String value){
        UserCookieInfo info = new UserCookieInfo();
        String[] keyValuePairs = value.split("\\|");
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            switch (entry[0]) {
                case "id" -> info.setId(Long.parseLong(entry[1]));
                case "login" -> info.setLogin(entry[1]);
                case "role" -> info.setRole(entry[1]);
            }
        }
        return info;
    }
}
