package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.ApartmentRentalInfo;
import com.example.dz_16_spring_book.dto.UserCookieInfo;
import com.example.dz_16_spring_book.models.*;
import com.example.dz_16_spring_book.repositories.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
public class RentalController {
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private ApartmentImageRepository apartmentImageRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ApartmentRentalRepository apartmentRentalRepository;

    @GetMapping("/rental")
    public String getApartmentsToRental(Model model){
        List<Apartment> apartments = getAllApartments();
        List<ApartmentImage> images = getAllApartmentImages();
        List<ApartmentRentalInfo> apartmentRentals = new ArrayList<>();
        List<District> districts = getAllDistrict();

        for(var apartment : apartments){
            ApartmentRentalInfo apartmentRental = new ApartmentRentalInfo();
            setApartmentRental(apartmentRental, apartment, districts, images);

            if(!apartment.isRented()){
                apartmentRentals.add(apartmentRental);
            }
        }

        model.addAttribute("rentals", apartmentRentals);
        model.addAttribute("districts", districts);
        return "rental";
    }

    @PostMapping("/rental/{id}/info")
    public String getApartmentInfo(@PathVariable(value = "id")Long id, Model model){
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        List<District> districts = getAllDistrict();
        List<ApartmentImage> images = getAllApartmentImages();
        ApartmentRentalInfo apartmentRental = new ApartmentRentalInfo();

        if(apartment != null){
            setApartmentRental(apartmentRental, apartment, districts, images);
        }

        model.addAttribute("rental", apartmentRental);
        return "rentalApartmentInfo";
    }

    @PostMapping("/rental/searchApartments")
    public String searchApartments(
            @RequestParam int rooms,
            @RequestParam Long district,
            @RequestParam Double price,
            Model model){
        List<Apartment> apartments = getAllApartments();
        List<ApartmentImage> images = getAllApartmentImages();
        List<ApartmentRentalInfo> apartmentRentals = new ArrayList<>();
        List<District> districts = getAllDistrict();
        District districtFromDB = districtRepository.findById(district).orElse(null);

        for(var apartment : apartments){
            ApartmentRentalInfo apartmentRental = new ApartmentRentalInfo();
            setApartmentRental(apartmentRental, apartment, districts, images);

            if(!apartment.isRented()){
                apartmentRentals.add(apartmentRental);
            }
        }

        var filterApartmentRental = apartmentRentals.stream()
                    .filter(apartment -> apartment.getBedrooms() == rooms && apartment.getRentAmount() < price)
                    .collect(Collectors.toList());

        if(district != 0 && districtFromDB != null){
             filterApartmentRental = filterApartmentRental.stream()
                     .filter(apartment -> apartment.getDistrict().equals(districtFromDB.getNameDistrict()))
                     .collect(Collectors.toList());
        }

        model.addAttribute("rentals", filterApartmentRental);
        model.addAttribute("districts", districts);
        return "rental";
    }

    @GetMapping("/rental/{id}/apartment")
    public String getRentalApartment(@PathVariable(name = "id") Long id, Model model){
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        if(apartment != null){
            model.addAttribute("rental", apartment);
        }
        return "rentalApartment";
    }

    @PostMapping("/rental/{id}/apartment")
    public String saveRentalApartment(@PathVariable(name = "id") Long id,
                                      @RequestParam(required = false) Integer months,
                                      @RequestParam(required = false) String dateRange,
                                      @RequestParam(required = false) String startDate,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes){
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        UserCookieInfo userCookieInfo = readCookies(request);
        Customer customer = new Customer();
        if(userCookieInfo != null){
            customer = customerRepository.findById(userCookieInfo.getId()).orElse(null);
        }
        ApartmentRental apartmentRental = new ApartmentRental();
        LocalDateTime now = LocalDateTime.now();

        if(dateRange != null){
            String[] dates = dateRange.split("-");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDateTime startDateTime = LocalDateTime.parse(dates[0].trim(), formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(dates[1].trim(), formatter);
            Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            if (startTimestamp.toLocalDateTime().isAfter(now) || startTimestamp.toLocalDateTime().isEqual(now)) {
                if(apartment != null){
                    apartmentRental.setCustomer(customer);
                    apartmentRental.setLandlord(apartment.getLandlord());
                    apartmentRental.setRentalStartDate(startTimestamp);
                    apartmentRental.setRentalEndDate(endTimestamp);
                    apartment.setRented(true);

                    apartmentRentalRepository.save(apartmentRental);
                    apartmentRepository.save(apartment);
                }

            } else {
                String message = "";
            }
        }else if(months != null && startDate != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate.trim(), formatter);
            Timestamp startTimestamp = Timestamp.valueOf(startDateTime);

            LocalDateTime endDateTime = startDateTime.plusMonths(months);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            if (startTimestamp.toLocalDateTime().isAfter(now) || startTimestamp.toLocalDateTime().isEqual(now)) {
                if(apartment != null){
                    apartmentRental.setCustomer(customer);
                    apartmentRental.setLandlord(apartment.getLandlord());
                    apartmentRental.setRentalStartDate(startTimestamp);
                    apartmentRental.setRentalEndDate(endTimestamp);
                    apartment.setRented(true);

                    apartmentRentalRepository.save(apartmentRental);
                    apartmentRepository.save(apartment);
                }
            }
        }
        return "redirect:/";
    }

    private void setApartmentRental(
            ApartmentRentalInfo apartmentRental,
            Apartment apartment,
            List<District> districts,
            List<ApartmentImage> images){
        apartmentRental.setId(apartment.getId());
        apartmentRental.setAddress(apartment.getAddress());
        apartmentRental.setRentAmount(apartment.getRentAmount());
        apartmentRental.setBedrooms(apartment.getBedrooms());
        apartmentRental.setDescription(apartment.getDescription());
        if(apartment.getDistrict() != null){
            for (var district : districts) {
                if(district.getId().equals(apartment.getDistrict().getId())){
                    apartmentRental.setDistrict(district.getNameDistrict());
                }
            }
        }
        apartmentRental.setRented(apartment.isRented());
        for(var image : images){
            if(image.getLinkImage() != null){
                if(apartment.getId().equals(image.getApartment().getId())){
                    apartmentRental.addToListImages(image.getLinkImage());
                }
            }
        }
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

    private List<Apartment> getAllApartments(){
        List<Apartment> apartments = new ArrayList<>();
        apartmentRepository.findAll().forEach(item -> apartments.add(item));
        return apartments;
    }

    private List<ApartmentImage> getAllApartmentImages(){
        List<ApartmentImage> apartmentImages = new ArrayList<>();
        apartmentImageRepository.findAll().forEach(item -> apartmentImages.add(item));
        return apartmentImages;
    }

    private List<District> getAllDistrict(){
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        return districts;
    }
}