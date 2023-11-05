package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.CustomerInfo;
import com.example.dz_16_spring_book.models.Customer;
import com.example.dz_16_spring_book.models.District;
import com.example.dz_16_spring_book.repositories.CustomerRepository;
import com.example.dz_16_spring_book.repositories.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class CustomerController {
    private static final Logger logger = Logger.getLogger(CustomerController.class.getName());
    private final CustomerRepository customerRepository;
    private final DistrictRepository districtRepository;

    @GetMapping("/customers")
    public String getAllCustomers(Model model){
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach(item -> customers.add(item));

        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));

        List<CustomerInfo> customerInfos = new ArrayList<>();
        for (var customer : customers) {
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setId(customer.getId());
            customerInfo.setFirstName(customer.getFirstName());
            customerInfo.setLastName(customer.getLastName());
            customerInfo.setEmail(customer.getEmail());
            customerInfo.setPhone(customer.getPhone());
            customerInfo.setAddress(customer.getAddress());
            customerInfo.setNumberRooms(customer.getNumberRooms());
            customerInfo.setFloor(customer.getFloor());
            customerInfo.setPrice(customer.getPrice());
            if(customer.getDistrict() != null){
                logger.info("DISTRICT: " + customer.getDistrict().getId());
                for (var district : districts) {
                    if(district.getId().equals(customer.getDistrict().getId())){
                        logger.info("NANE DISTRICT: " + district.getNameDistrict());
                        customerInfo.setDistrict(district.getNameDistrict());
                    }
                }
            }

            customerInfos.add(customerInfo);
        }
        model.addAttribute("customers", customerInfos);
        return "customers";
    }
}
