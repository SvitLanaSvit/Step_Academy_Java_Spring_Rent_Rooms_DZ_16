package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.models.Landlord;
import com.example.dz_16_spring_book.repositories.LandlordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LandlordController {
    private final LandlordRepository landlordRepository;

    @GetMapping("/landlords")
    public String getAllLandlords(Model model){
        List<Landlord> landlords = new ArrayList<>();
        landlordRepository.findAll().forEach(item -> landlords.add(item));
        model.addAttribute("landlords", landlords);
        return "landlords";
    }
}
