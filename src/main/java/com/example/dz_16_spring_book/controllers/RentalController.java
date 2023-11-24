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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private CustomerRepository customerRepository;
    @Autowired
    private ApartmentRentalRepository apartmentRentalRepository;
    @Autowired
    private UserRepository userRepository;

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
        Map<String, String> constMonths = new HashMap<>();
        constMonths.put("January", "01");
        constMonths.put("February", "02");
        constMonths.put("March", "03");
        constMonths.put("April", "04");
        constMonths.put("May", "05");
        constMonths.put("June", "06");
        constMonths.put("July", "07");
        constMonths.put("August", "08");
        constMonths.put("September", "09");
        constMonths.put("October", "10");
        constMonths.put("November", "11");
        constMonths.put("December", "12");

        logger.info("ID: " + id);
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        UserCookieInfo userCookieInfo = readCookies(request);
        Customer customer = new Customer();
        if(userCookieInfo != null){
            List<User> users = getAllUsers();
            User user = users.stream().filter(item -> userCookieInfo.getId().equals(item.getId())).toList().get(0);
            customer = customerRepository.findById(user.getCustomer().getId()).orElse(null);
        }
        ApartmentRental apartmentRental = new ApartmentRental();
        LocalDateTime startOfTomorrow = LocalDate.now().plusDays(1).atStartOfDay();

        if(dateRange != null){
            String[] dates = dateRange.split("-");
            String startDateStr = getDateRegex(constMonths, dates[0].trim());
            String endDateStr = getDateRegex(constMonths, dates[1].trim());
            Timestamp startTimestamp = null;
            Timestamp endTimestamp = null;
            if(startDateStr != null){
                LocalDate date = LocalDate.parse(startDateStr);
                LocalDateTime dateTime = date.atStartOfDay(); // Assuming the start of the day for the timestamp
                startTimestamp = Timestamp.valueOf(dateTime);
            }

            if(endDateStr != null){
                LocalDate date = LocalDate.parse(endDateStr);
                LocalDateTime dateTime = date.atStartOfDay(); // Assuming the end of the day for the timestamp
                endTimestamp = Timestamp.valueOf(dateTime);
            }

            if(startTimestamp != null && endTimestamp != null){
                if (startTimestamp.toLocalDateTime().isAfter(startOfTomorrow) ||
                        startTimestamp.toLocalDateTime().isEqual(startOfTomorrow)) {
                    if(apartment != null){
                        apartmentRental.setCustomer(customer);
                        apartmentRental.setLandlord(apartment.getLandlord());
                        apartmentRental.setRentalStartDate(startTimestamp);
                        apartmentRental.setRentalEndDate(endTimestamp);
                        apartmentRental.setApartment(apartment);
                        apartment.setRented(true);

                        apartmentRentalRepository.save(apartmentRental);
                        apartmentRepository.save(apartment);

                        String messageSuccess = "Rental is successful!";
                        redirectAttributes.addFlashAttribute("messageSuccess", messageSuccess);
                    }

                } else {
                    String message = "This date " + startTimestamp +" cannot be lower than the current date!";
                    redirectAttributes.addFlashAttribute("message", message);
                }
            }
        }else if(months != null && startDate != null){
            String[] startDateArray = startDate.trim().split("/");
            String month = startDateArray[0];
            String day = String.format("%02d", Integer.parseInt(startDateArray[1]));
            String year = startDateArray[2];
            String startDateStr = year + '-' + month + '-' + day;

            LocalDate date = LocalDate.parse(startDateStr);
            LocalDateTime dateTime = date.atStartOfDay(); // Assuming the start of the day for the timestamp
            Timestamp startTimestamp = Timestamp.valueOf(dateTime);

            LocalDateTime endDateTime = dateTime.plusMonths(months);
            Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

            if (startTimestamp.toLocalDateTime().isAfter(startOfTomorrow) ||
                    startTimestamp.toLocalDateTime().isEqual(startOfTomorrow)) {
                if(apartment != null){
                    apartmentRental.setCustomer(customer);
                    apartmentRental.setLandlord(apartment.getLandlord());
                    apartmentRental.setRentalStartDate(startTimestamp);
                    apartmentRental.setRentalEndDate(endTimestamp);
                    apartmentRental.setApartment(apartment);
                    apartment.setRented(true);

                    apartmentRentalRepository.save(apartmentRental);
                    apartmentRepository.save(apartment);

                    String messageSuccess = "Rental is successful!";
                    redirectAttributes.addFlashAttribute("messageSuccess", messageSuccess);
                }
            }else{
                String message = "This date " + startTimestamp +" cannot be lower than the current date!";
                redirectAttributes.addFlashAttribute("message", message);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/rental/canselLease")
    public String canselLeaseApartment(@RequestParam Long rentalId, @RequestParam Long apartmentId){
        var apartmentRental = apartmentRentalRepository .findById(rentalId).orElse(null);
        var apartment = apartmentRepository.findById(apartmentId).orElse(null);
        if(apartmentRental != null){
            apartmentRentalRepository.delete(apartmentRental);

            if(apartment != null){
                apartment.setRented(false);
                apartmentRepository.save(apartment);
            }
        }

        return "redirect:/";
    }
    private List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(item -> users.add(item));
        return users;
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

    private String getDateRegex(Map<String, String> map, String date){
        String datePattern = "(\\w+) (\\d{1,2}), (\\d{4})";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(date);

        if (matcher.find()) {
            String month = matcher.group(1);
            String day = String.format("%02d", Integer.parseInt(matcher.group(2))); // Ensure the day is in two-digit format
            String year = matcher.group(3);

            return year + '-' + map.get(month) + '-' + day;
        }
        return null;
    }
}