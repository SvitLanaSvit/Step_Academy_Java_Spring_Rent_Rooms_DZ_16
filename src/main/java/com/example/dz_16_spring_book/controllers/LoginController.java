package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.UserCookieInfo;
import com.example.dz_16_spring_book.models.*;
import com.example.dz_16_spring_book.repositories.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class LoginController {
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LandlordRepository landlordRepository;

    @GetMapping("/registration")
    public String getRegistration(Model model){
        List<District> districts = getAllDistricts();
        model.addAttribute("districts",districts);
        return "login/registration";
    }

    @PostMapping("/registration")
    public String saveNewUserAfterRegistration(
            @RequestParam String role,
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) Integer numberRooms,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Long district,
            Model model,
            RedirectAttributes redirectAttributes){

        boolean isSaved = false;

        if("customer".equals(role)){
            List<Customer> customers = getAllCustomers();
            District districtFromDB = districtRepository.findById(district).orElse(null);
            Customer customer = new Customer();
            customer.setFirstName(firstname);
            customer.setLastName(lastname);
            boolean isExistsEmail = customers.stream().anyMatch(custom -> email.equals(custom.getEmail()));
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setNumberRooms(numberRooms);
            customer.setFloor(floor);
            customer.setPrice(price);
            if(districtFromDB != null){
                customer.setDistrict(districtFromDB);
            }

            if(!isExistsEmail){
                customer.setEmail(email);
                customerRepository.save(customer);
                isSaved = true;
            }else{
                String message = "This email '" + email + "' exists already!";
                redirectAttributes.addFlashAttribute("message", message);
            }

            if(isSaved){
                User user = new User();
                Customer customerFromDB = customerRepository.findByEmail(email).orElse(null);
                List<Role> roles = getAllRoles();
                Role roleFromDB = roles.stream().filter(item -> role.equals(item.getRoleName())).toList().get(0);
                List<User> users = getAllUsers();
                boolean isExistsLogin = users.stream().anyMatch(item -> login.equals(item.getLogin()));
                if(customerFromDB != null){
                    if(!isExistsLogin){
                        user.setLogin(login);
                        user.setCustomer(customerFromDB);
                        user.setPassword(hashPassword(password));
                        user.setRole(roleFromDB);
                        userRepository.save(user);
                        String messageSuccess = "Registration is successful!";
                        redirectAttributes.addFlashAttribute("messageSuccess", messageSuccess);
                    }else{
                        String message = "This login '" + login + "' exists already!";
                        redirectAttributes.addFlashAttribute("message", message);
                    }
                }
            }
        }else if("landlord".equals(role)){
            List<Landlord> landlords = getAllLandlords();
            Landlord landlord = new Landlord();
            landlord.setFirstName(firstname);
            landlord.setLastName(lastname);
            boolean isExistsEmail = landlords.stream().anyMatch(land -> email.equals(land.getEmail()));
            landlord.setPhone(phone);
            landlord.setAddress(address);

            if(!isExistsEmail){
                landlord.setEmail(email);
                landlordRepository.save(landlord);
                isSaved = true;
            }else{
                String message = "This email '" + email + "' exists already!";
                redirectAttributes.addFlashAttribute("message", message);
            }

            if(isSaved){
                User user = new User();
                Landlord landlordFromDB = landlordRepository.findByEmail(email).orElse(null);
                List<Role> roles = getAllRoles();
                Role roleFromDB = roles.stream().filter(item -> role.equals(item.getRoleName())).toList().get(0);
                List<User> users = getAllUsers();
                boolean isExistsLogin = users.stream().anyMatch(item -> login.equals(item.getLogin()));
                if(landlordFromDB != null) {
                    if (!isExistsLogin) {
                        user.setLogin(login);
                        user.setLandlord(landlordFromDB);
                        user.setPassword(hashPassword(password));
                        user.setRole(roleFromDB);
                        userRepository.save(user);
                        String messageSuccess = "Registration is successful!";
                        redirectAttributes.addFlashAttribute("messageSuccess", messageSuccess);
                    } else {
                        String message = "This login '" + login + "' exists already!";
                        redirectAttributes.addFlashAttribute("message", message);
                    }
                }
            }
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String getLogin(){
        return "login/login";
    }

    @PostMapping("/login")
    public String makeLogin(
            @RequestParam String login,
            @RequestParam String password,
            RedirectAttributes redirectAttributes){
        List<User> users = getAllUsers();
        User user = users.stream().filter(item -> login.equals(item.getLogin())).toList().get(0);
        if(user != null){
            logger.info("USER: " + user.getId() + ' ' + user.getLogin());
            if(verifyPassword(password, user.getPassword()) && login.equals(user.getLogin())){
                redirectAttributes
                        .addFlashAttribute("user", "id:" + user.getId() +
                                "|login:" + user.getLogin() +
                                "|role:" + user.getRole().getRoleName());
                String messageSuccess = "Log in is successful!";
                redirectAttributes.addFlashAttribute("messageSuccess", messageSuccess);
                if(user.getCustomer() != null){
                    Customer customer = customerRepository.findById(user.getCustomer().getId()).orElse(null);
                    if(customer != null){
                        logger.info("CUSTOMER: " + customer.getId() + ',' + customer.getFirstName());
                        redirectAttributes.addFlashAttribute("userInfo", customer);
                    }

                }else if(user.getLandlord() != null){
                    landlordRepository.findById(user.getLandlord().getId())
                            .ifPresent(landlord -> redirectAttributes.addFlashAttribute("userInfo", landlord));
                }

            }
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String makeLogout(HttpServletResponse response){
        // Create a cookie with the same name and set its max age to 0 to delete it
        Cookie cookie = new Cookie("log", null);
        cookie.setPath("/"); // Make sure to set the path exactly as it was set when the cookie was created
        cookie.setHttpOnly(true); // Optional: If you have set HttpOnly when creating the cookie, you should also set it here
        cookie.setMaxAge(0); // Set the cookie's age to 0 to delete it

        // Add the cookie to the response to send it back to the browser
        response.addCookie(cookie);
        return "redirect:/";
    }

    private List<District> getAllDistricts(){
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        return districts;
    }
    private List<Customer> getAllCustomers(){
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach(item -> customers.add(item));
        return customers;
    }
    private List<Role> getAllRoles(){
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().forEach(item -> roles.add(item));
        return roles;
    }
    private List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(item -> users.add(item));
        return users;
    }
    private List<Landlord> getAllLandlords(){
        List<Landlord> landlords = new ArrayList<>();
        landlordRepository.findAll().forEach(item -> landlords.add(item));
        return  landlords;
    }
    private String hashPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
    private boolean verifyPassword(String plainPassword, String storeHash){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(plainPassword, storeHash);
    }
}