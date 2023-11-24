package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.ApartmentRentalCustomerInfo;
import com.example.dz_16_spring_book.dto.CustomerInfo;
import com.example.dz_16_spring_book.dto.UserUpdateDesire;
import com.example.dz_16_spring_book.models.*;
import com.example.dz_16_spring_book.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ApartmentRentalRepository apartmentRentalRepository;
    private final ApartmentImageRepository apartmentImageRepository;
    private final UserRepository userRepository;

    @GetMapping("/customers")
    public String getAllCustomers(Model model) {
        List<Customer> customers = getAllCustomer();
        List<District> districts = getAllDistricts();

        List<CustomerInfo> customerInfos = setCustomerInfos(customers, districts);
        model.addAttribute("customers", customerInfos);
        model.addAttribute("districts", districts);
        return "customers";
    }

    @PostMapping("/customers/searchByName")
    public String searchByName(@RequestParam String fullname, Model model) {
        List<Customer> customers = getAllCustomer();
        List<District> districts = getAllDistricts();
        List<CustomerInfo> customerInfos;

        if (fullname != null) {
            List<Customer> filteredCustomers = customers
                    .stream()
                    .filter(item -> (item.getFirstName() + ' ' + item.getLastName()).toLowerCase()
                            .contains(fullname.toLowerCase())).toList();
            customerInfos = setCustomerInfos(filteredCustomers, districts);
            model.addAttribute("customers", customerInfos);
        }

        model.addAttribute("districts", districts);
        return "customers";
    }

    @PostMapping("/customers/searchByPhone")
    public String searchByPhone(@RequestParam String phoneNumber, Model model) {
        List<Customer> customers = getAllCustomer();
        List<District> districts = getAllDistricts();
        List<CustomerInfo> customerInfos;

        if (phoneNumber != null) {
            List<Customer> filteredCustomers = customers
                    .stream()
                    .filter(item -> item.getPhone().equals(phoneNumber))
                    .toList();
            customerInfos = setCustomerInfos(filteredCustomers, districts);
            model.addAttribute("customers", customerInfos);
        }
        model.addAttribute("districts", districts);
        return "customers";
    }

    @PostMapping("/customers/searchByAnother")
    public String searchByAnother(
            @RequestParam int rooms,
            @RequestParam String district,
            @RequestParam double price,
            Model model) {
        List<Customer> customers = getAllCustomer();
        List<District> districts = getAllDistricts();
        List<Customer> filteredCustomers = new ArrayList<>();

        Long id = Long.parseLong(district);

        for (var customer : customers) {
            if (customer.getNumberRooms() == rooms
                    && customer.getDistrict().getId().equals(id)
                    && customer.getPrice() == price) {
                filteredCustomers.add(customer);
            }
        }
        model.addAttribute("districts", districts);
        model.addAttribute("customers", filteredCustomers);
        return "customers";
    }

    @GetMapping("/customer/{id}/info")
    public String getInfo(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        Customer customer = customerRepository.findById(id).orElse(null);
        CustomerInfo customerInfo = new CustomerInfo();
        if (customer != null) {
            customerInfo.setId(id);
            customerInfo.setFirstName(customer.getFirstName());
            customerInfo.setLastName(customer.getLastName());
            customerInfo.setEmail(customer.getEmail());
            customerInfo.setPhone(customer.getPhone());
            customerInfo.setAddress(customer.getAddress());
            customerInfo.setNumberRooms(customer.getNumberRooms());
            customerInfo.setFloor(customer.getFloor());
            customerInfo.setPrice(customer.getPrice());
            customerInfo.setDistrict(customer.getDistrict().getNameDistrict());
        }
        redirectAttributes.addFlashAttribute("customerInfoHome", customerInfo);
        return "redirect:/";
    }

    @GetMapping("/customer/{id}/updateInfo")
    public String getUpdateInfo(@PathVariable(name = "id") Long id, Model model) {
        Customer customer = customerRepository.findById(id).orElse(null);
        List<District> districts = getAllDistricts();
        model.addAttribute("customer", customer);
        model.addAttribute("districts", districts);
        return "customerInfoUpdate";
    }

    @PostMapping("/customer/{id}/updateInfo")
    public String saveUpdateInfo(@PathVariable(name = "id") Long id, @ModelAttribute("user") UserUpdateDesire user) {
        Customer customer = customerRepository.findById(id).orElse(null);
        District district = districtRepository.findById(user.getDistrict()).orElse(null);
        if (customer != null) {
            if (user.getNumberRooms() != customer.getNumberRooms()) {
                customer.setNumberRooms(user.getNumberRooms());
            }

            if (user.getFloor() != customer.getFloor()) {
                customer.setFloor(user.getFloor());
            }

            if (user.getPrice() != customer.getPrice()) {
                customer.setPrice(user.getPrice());
            }

            if (district != null) {
                if (!user.getDistrict().equals(customer.getDistrict().getId())) {
                    customer.setDistrict(district);
                }
            }

            customerRepository.save(customer);
        }

        return "redirect:/";
    }

    @GetMapping("/customer/{id}/infoRental")
    public String getInfoRental(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("ID: " + id);
        List<ApartmentRental> apartmentRentals = getApartmentRentals();
        var filteredApartmentRental = apartmentRentals.stream().filter(item -> id.equals(item.getCustomer().getId())).toList();
        List<ApartmentRentalCustomerInfo> apartmentRentalCustomerInfos = new ArrayList<>();
        List<ApartmentImage> apartmentImages = getApartmentImages();

        for (var apartmentRental : filteredApartmentRental) {
            ApartmentRentalCustomerInfo info = new ApartmentRentalCustomerInfo();
            info.setId(apartmentRental.getId());
            info.setIdLandlord(apartmentRental.getLandlord().getId());
            info.setFullNameLandlord(apartmentRental.getLandlord().getFirstName() +
                    " " + apartmentRental.getLandlord().getLastName());
            info.setEmailLandlord(apartmentRental.getLandlord().getEmail());
            info.setPhoneLandlord(apartmentRental.getLandlord().getPhone());
            info.setIdApartment(apartmentRental.getApartment().getId());
            info.setAddressApartment(apartmentRental.getApartment().getAddress());
            info.setRentAmountApartment(apartmentRental.getApartment().getRentAmount());
            info.setBedroomsApartment(apartmentRental.getApartment().getBedrooms());
            info.setDescriptionApartment(apartmentRental.getApartment().getDescription());
            info.setDistrictApartment(apartmentRental.getApartment().getDistrict().getNameDistrict());
            var filteredApartmentImages = apartmentImages.stream()
                    .filter(item -> apartmentRental.getApartment().getId().equals(item.getApartment().getId()))
                    .toList();
            List<String> images = new ArrayList<>();
            for (var image : filteredApartmentImages) {
                images.add(image.getLinkImage());
            }
            info.setLinkImagesApartment(images.get(0));
            apartmentRentalCustomerInfos.add(info);
            info.setRentalDate(apartmentRental.getRentalDate());
            info.setStartRentalDate(apartmentRental.getRentalStartDate());
            info.setEndRentalDate(apartmentRental.getRentalEndDate());
        }
        redirectAttributes.addFlashAttribute("rentalCustomerInfos", apartmentRentalCustomerInfos);
        return "redirect:/";
    }

    private List<ApartmentImage> getApartmentImages() {
        List<ApartmentImage> apartmentImages = new ArrayList<>();
        apartmentImageRepository.findAll().forEach(item -> apartmentImages.add(item));
        return apartmentImages;
    }

    private List<ApartmentRental> getApartmentRentals() {
        List<ApartmentRental> apartmentRentals = new ArrayList<>();
        apartmentRentalRepository.findAll().forEach(item -> apartmentRentals.add(item));
        return apartmentRentals;
    }

    private List<Customer> getAllCustomer() {
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach(item -> customers.add(item));
        return customers;
    }

    private List<District> getAllDistricts() {
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        return districts;
    }

    private List<CustomerInfo> setCustomerInfos(List<Customer> customers, List<District> districts) {
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
            if (customer.getDistrict() != null) {
                logger.info("DISTRICT: " + customer.getDistrict().getId());
                for (var district : districts) {
                    if (district.getId().equals(customer.getDistrict().getId())) {
                        logger.info("NANE DISTRICT: " + district.getNameDistrict());
                        customerInfo.setDistrict(district.getNameDistrict());
                    }
                }
            }

            customerInfos.add(customerInfo);
        }
        return customerInfos;
    }
}