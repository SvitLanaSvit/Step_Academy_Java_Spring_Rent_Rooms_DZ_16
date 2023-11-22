package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.UserHome;
import com.example.dz_16_spring_book.models.Customer;
import com.example.dz_16_spring_book.models.Landlord;
import com.example.dz_16_spring_book.models.User;
import com.example.dz_16_spring_book.repositories.CustomerRepository;
import com.example.dz_16_spring_book.repositories.LandlordRepository;
import com.example.dz_16_spring_book.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    LandlordRepository landlordRepository;

    @GetMapping("/userHome/{id}/update")
    public String getUserUpdate(@PathVariable(name = "id") Long id, Model model){
        User userToUpdate = getUser(id);

        if(userToUpdate != null){
            if("customer".equals(userToUpdate.getRole().getRoleName())){
                Customer customer = customerRepository.findById(id).orElse(null);
                model.addAttribute("user", customer);
            }else if("landlord".equals(userToUpdate.getRole().getRoleName())){
                Landlord landlord = landlordRepository.findById(id).orElse(null);
                model.addAttribute("user", landlord);
            }
        }
        return "updateUserHome";
    }

    @PostMapping("/userHome/{id}/update")
    public String saveUserUpdate(@ModelAttribute("user") UserHome user, @PathVariable(name = "id")Long id){
        User userToUpdate = getUser(id);

        if(userToUpdate != null){
            if("customer".equals(userToUpdate.getRole().getRoleName()) || "admin".equals(userToUpdate.getRole().getRoleName())){
                Customer customer = customerRepository.findById(id).orElse(null);
                if(customer != null){
                    if(!user.getFirstname().equals(customer.getFirstName())){
                        customer.setFirstName(user.getFirstname());
                    }

                    if(!user.getLastname().equals(customer.getLastName())){
                        customer.setLastName(user.getLastname());
                    }

                    if(!user.getEmail().equals(customer.getEmail())){
                        customer.setEmail(user.getEmail());
                    }

                    if(!user.getPhone().equals(customer.getPhone())){
                        customer.setPhone(user.getPhone());
                    }

                    if(!user.getAddress().equals(customer.getAddress())){
                        customer.setAddress(user.getAddress());
                    }

                    customerRepository.save(customer);
                }
            }else if("landlord".equals(userToUpdate.getRole().getRoleName())){
                Landlord landlord = landlordRepository.findById(id).orElse(null);
                if(landlord != null){
                    if(!user.getFirstname().equals(landlord.getFirstName())){
                        landlord.setFirstName(user.getFirstname());
                    }

                    if(!user.getLastname().equals(landlord.getLastName())){
                        landlord.setLastName(user.getLastname());
                    }

                    if(!user.getEmail().equals(landlord.getEmail())){
                        landlord.setEmail(user.getEmail());
                    }

                    if(!user.getPhone().equals(landlord.getPhone())){
                        landlord.setPhone(user.getPhone());
                    }

                    if(!user.getAddress().equals(landlord.getAddress())){
                        landlord.setAddress(user.getAddress());
                    }

                    landlordRepository.save(landlord);
                }
            }
        }
        return "redirect:/";
    }

    private List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(item -> users.add(item));
        return users;
    }

    private User getUser(Long id){
        List<User> users = getAllUsers();
        return users.stream()
                .filter(item -> (item.getCustomer() != null && item.getCustomer().getId().equals(id)) ||
                        (item.getLandlord() != null && item.getLandlord().getId().equals(id)))
                .findFirst().orElse(null);
    }
}
