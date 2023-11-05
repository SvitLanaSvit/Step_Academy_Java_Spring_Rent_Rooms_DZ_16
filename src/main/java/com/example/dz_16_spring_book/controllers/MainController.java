package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.models.Customer;
import com.example.dz_16_spring_book.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String getHome(@RequestParam(name = "text", required = false, defaultValue = "INTRODUCTION")String text,
                          Model model){
        model.addAttribute("text", text);
        return "home";
    }
}
