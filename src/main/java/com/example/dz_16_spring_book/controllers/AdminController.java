package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.CustomerInfo;
import com.example.dz_16_spring_book.models.Customer;
import com.example.dz_16_spring_book.models.District;
import com.example.dz_16_spring_book.models.Landlord;
import com.example.dz_16_spring_book.repositories.CustomerRepository;
import com.example.dz_16_spring_book.repositories.DistrictRepository;
import com.example.dz_16_spring_book.repositories.LandlordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private static final Logger logger = Logger.getLogger(AdminController.class.getName());
    private final CustomerRepository customerRepository;
    private final LandlordRepository landlordRepository;
    private final DistrictRepository districtRepository;

    @GetMapping("/admin")
    public String getAdminPage(Model model){
        return "admin";
    }

    @GetMapping("/admin/customers")
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
        return "adminCustomers";
    }

    @GetMapping("/customers/newCustomer")
    public String getNewCustomer(Model model){
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        model.addAttribute("districts", districts);
        return "newCustomer";
    }

    @PostMapping("/customers/newCustomer")
    public String saveNewCustomer(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam int numberRooms,
            @RequestParam int floor,
            @RequestParam double price,
            @RequestParam Long district,
            Model model){
        var districtSearch = districtRepository.findById(district).orElse(null);

        Customer customer = new Customer();
        customer.setFirstName(firstname);
        customer.setLastName(lastname);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setNumberRooms(numberRooms);
        customer.setFloor(floor);
        customer.setPrice(price);
        customer.setDistrict(districtSearch);

        customerRepository.save(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/customer/{id}/update")
    public String getCustomerUpdate(@PathVariable(value = "id") Long id, Model model){
        customerRepository.findById(id).ifPresent(customer -> model.addAttribute("customer", customer));
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        model.addAttribute("districts", districts);
        return "updateCustomer";
    }

    @PostMapping("/customer/{id}/update")
    public String saveCustomerUpdate(@PathVariable(value = "id") Long id,
                                     @RequestParam String firstname,
                                     @RequestParam String lastname,
                                     @RequestParam String email,
                                     @RequestParam String phone,
                                     @RequestParam String address,
                                     @RequestParam int numberRooms,
                                     @RequestParam int floor,
                                     @RequestParam double price,
                                     @RequestParam Long district,
                                     Model model){
        District districtObj = districtRepository.findById(district).orElse(null);
        Customer customer = customerRepository.findById(id).orElse(null);
        if(customer != null){
            if(!firstname.equals(customer.getFirstName())){
                customer.setFirstName(firstname);
            }

            if(!lastname.equals(customer.getLastName())){
                customer.setLastName(lastname);
            }

            if(!email.equals(customer.getEmail())){
                customer.setEmail(email);
            }

            if(!phone.equals(customer.getPhone())){
                customer.setPhone(phone);
            }

            if(!address.equals(customer.getAddress())){
                customer.setAddress(address);
            }

            if(numberRooms != customer.getNumberRooms()){
                customer.setNumberRooms(numberRooms);
            }

            if(floor != customer.getFloor()){
                customer.setFloor(floor);
            }

            if(price != customer.getPrice()){
                customer.setPrice(price);
            }

            assert districtObj != null;
            if(!districtObj.equals(customer.getDistrict())){
                customer.setDistrict(districtObj);
            }
        }
        assert customer != null;
        customerRepository.save(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/landlords/newLandlord")
    public String getNewLandlord(Model model){
        return "newLandlord";
    }

    @PostMapping("/landlords/newLandlord")
    public String saveNewLandlord(
            @RequestParam String firstname,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            Model model){
        Landlord landlord = new Landlord();
        landlord.setFirstName(firstname);
        landlord.setLastName(lastname);
        landlord.setEmail(email);
        landlord.setPhone(phone);
        landlord.setAddress(address);

        landlordRepository.save(landlord);
        return "redirect:/landlords";
    }

    @GetMapping("/district/newDistrict")
    public String getNewDistrict(Model model){
        return "newDistrict";
    }

    @PostMapping("/district/newDistrict")
    public String saveNewDistrict(
            @RequestParam String nameDistrict,
            Model model){
        District district = new District();
        district.setNameDistrict(nameDistrict);

        districtRepository.save(district);
        return "redirect:/admin";
    }

}
