package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.models.District;
import com.example.dz_16_spring_book.repositories.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private DistrictRepository districtRepository;

    @GetMapping("/registration")
    public String getRegistration(Model model){
        List<District> districts = getAllDistricts();
        model.addAttribute("districts",districts);
        return "login/registration";
    }

    @PostMapping("/registration")
    public String saveNewUserAfterRegistration(
            String login,
            String password,
            Model model,
            RedirectAttributes redirectAttributes){
        return null;
    }

    private List<District> getAllDistricts(){
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        return districts;
    }
}
