package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.ApartmentInfo;
import com.example.dz_16_spring_book.dto.CustomerInfo;
import com.example.dz_16_spring_book.models.*;
import com.example.dz_16_spring_book.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final ApartmentRepository apartmentRepository;
    private final RoleRepository roleRepository;

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

            if(!districtObj.equals(customer.getDistrict())){
                customer.setDistrict(districtObj);
            }
        }
        assert customer != null;
        customerRepository.save(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/customer/{id}/remove")
    public String getRemove(@PathVariable(value = "id") Long id, Model model){
        customerRepository.findById(id).ifPresent(customerRepository::delete);
        return "redirect:/admin/customers";
    }

    @GetMapping("/admin/landlords")
    public String getAllLandlords(Model model){
        List<Landlord> landlords = getAllLandlords();
        model.addAttribute("landlords", landlords);
        return "adminLandlords";
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
        return "redirect:/admin/landlords";
    }

    @GetMapping("/landlord/{id}/update")
    public String getLandlordUpdate(@PathVariable(value = "id") Long id, Model model){
        Landlord landlord = landlordRepository.findById(id).orElse(null);
        model.addAttribute("landlord", landlord);
        return "updateLandlord";
    }

    @PostMapping("/landlord/{id}/update")
    public String saveLandlordUpdate(@PathVariable(value = "id")Long id,
                                     @RequestParam String firstname,
                                     @RequestParam String lastname,
                                     @RequestParam String email,
                                     @RequestParam String phone,
                                     @RequestParam String address,
                                     Model model){
        Landlord landlord = landlordRepository.findById(id).orElse(null);
        assert landlord != null;
        if(!firstname.equals(landlord.getFirstName())){
            landlord.setFirstName(firstname);
        }

        if(!lastname.equals(landlord.getLastName())){
            landlord.setLastName(lastname);
        }

        if(!email.equals(landlord.getEmail())){
            landlord.setEmail(email);
        }

        if(!phone.equals(landlord.getPhone())){
            landlord.setPhone(phone);
        }

        if(!address.equals(landlord.getAddress())){
            landlord.setAddress(address);
        }

        landlordRepository.save(landlord);
        return "redirect:/admin/landlords";
    }

    @GetMapping("/landlord/{id}/remove")
    public String removeLandlord(@PathVariable(value = "id") Long id, Model model){
        landlordRepository.findById(id).ifPresent(landlordRepository::delete);
        return "redirect:/admin/landlords";
    }

    @GetMapping("/admin/districts")
    private String getAllDistricts(Model model){
        List<District> districts = getAllDistricts();
        model.addAttribute("districts", districts);
        var attr = model.getAttribute("message");
        if(!model.containsAttribute("message")){
            model.addAttribute("message", null);
        }
        return "adminDistricts";
    }

    @GetMapping("/districts/newDistrict")
    public String getNewDistrict(Model model){
        return "newDistrict";
    }

    @PostMapping("/districts/newDistrict")
    public String saveNewDistrict(
            @RequestParam String nameDistrict,
            RedirectAttributes redirectAttributes){
        District district = new District();
        List<District> districts = getAllDistricts();
        var isExists = districts.stream().anyMatch(dist -> nameDistrict.equals(dist.getNameDistrict()));
        if(!isExists){
            district.setNameDistrict(nameDistrict);
            districtRepository.save(district);
        }else{
            String message = "This district '" + nameDistrict + "' exists already!";
            redirectAttributes.addFlashAttribute("message", message);
        }

        return "redirect:/admin/districts";
    }

    @GetMapping("/district/{id}/update")
    public String getDistrictUpdate(@PathVariable(value = "id") Long id, Model model){
        District district = districtRepository.findById(id).orElse(null);
        model.addAttribute("district", district);
        return "updateDistrict";
    }

    @PostMapping("/district/{id}/update")
    public String saveDistrictUpdate(@PathVariable(value = "id") Long id, @RequestParam String district, Model model){
        District districtFromDB = districtRepository.findById(id).orElse(null);

        List<District> districts = getAllDistricts();
        if(districtFromDB != null){
            if(!district.equals(districtFromDB.getNameDistrict())){
                districtFromDB.setNameDistrict(district);
            }
        }

        boolean isExists = districts.stream().anyMatch(dist -> district.equals(dist.getNameDistrict()));

        if(districtFromDB != null && !isExists){
            districtRepository.save(districtFromDB);
        }
        return "redirect:/admin/districts";
    }

    @GetMapping("/district/{id}/remove")
    private String removeDistrict(@PathVariable(value = "id")Long id, Model model){
        districtRepository.findById(id).ifPresent(districtRepository::delete);
        return "redirect:/admin/districts";
    }

    @GetMapping("/admin/apartments")
    public String getAllApartments(Model model){
        List<Apartment> apartments = getAllApartments();
        List<Landlord> landlords = getAllLandlords();
        List<District> districts = getAllDistricts();

        List<ApartmentInfo> apartmentInfos = new ArrayList<>();
        for(var apartment : apartments){
            ApartmentInfo apartmentInfo = new ApartmentInfo();
            apartmentInfo.setId(apartment.getId());
            apartmentInfo.setAddress(apartment.getAddress());
            apartmentInfo.setBedrooms(apartment.getBedrooms());
            apartmentInfo.setRentAmount(apartment.getRentAmount());
            apartmentInfo.setDescription(apartment.getDescription());
            if(apartment.getLandlord() != null){
                for (var landlord : landlords) {
                    if(landlord.getId().equals(apartment.getLandlord().getId())){
                        logger.info("NEW LANDLORD: " + landlord.getFirstName() + " " + landlord.getLastName());
                        apartmentInfo.setLandlord(landlord.getFirstName() + " " + landlord.getLastName());
                    }
                }
            }

            if(apartment.getDistrict() != null){
                for(var district : districts){
                    if(district.getId().equals(apartment.getDistrict().getId())){
                        apartmentInfo.setDistrict(district.getNameDistrict());
                    }
                }
            }
            apartmentInfo.setRented(apartment.isRented());
            apartmentInfos.add(apartmentInfo);
        }
        model.addAttribute("apartments", apartmentInfos);
        return "adminApartments";
    }

    @GetMapping("/apartments/newApartment")
    public String getNewApartment(Model model){
        List<Landlord> landlords = getAllLandlords();
        List<District> districts = getAllDistricts();
        model.addAttribute("landlords", landlords);
        model.addAttribute("districts", districts);
        return "newApartment";
    }

    @PostMapping("/apartments/newApartment")
    public String saveNewApartment(
            @RequestParam String address,
            @RequestParam int bedrooms,
            @RequestParam double rentAmount,
            @RequestParam Long landlord,
            @RequestParam String description,
            @RequestParam Long district,
            Model model){
        Landlord landlordFromDB = landlordRepository.findById(landlord).orElse(null);
        District districtFromDB = districtRepository.findById(district).orElse(null);
        Apartment apartment = new Apartment();
        apartment.setAddress(address);
        apartment.setBedrooms(bedrooms);
        apartment.setRentAmount(rentAmount);
        apartment.setLandlord(landlordFromDB);
        apartment.setDescription(description);
        apartment.setDistrict(districtFromDB);

        apartmentRepository.save(apartment);
        return "redirect:/admin/apartments";
    }

    @GetMapping("/apartment/{id}/update")
    public String getApartmentUpdate(@PathVariable(value = "id") Long id, Model model){
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        List<Landlord> landlords = getAllLandlords();
        List<District> districts = getAllDistricts();
        model.addAttribute("apartment", apartment);
        model.addAttribute("landlords", landlords);
        model.addAttribute("districts", districts);
        return "updateApartment";
    }

    @PostMapping("/apartment/{id}/update")
    public String saveApartmentUpdate(@PathVariable(value = "id")Long id,
                                      @RequestParam Long landlord,
                                      @RequestParam String address,
                                      @RequestParam int bedrooms,
                                      @RequestParam Double rentAmount,
                                      @RequestParam String description,
                                      @RequestParam Long district,
                                      Model model){
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        Landlord landlordObj = landlordRepository.findById(landlord).orElse(null);
        District districtObj = districtRepository.findById(district).orElse(null);
        if(apartment != null){
            if(!address.equals(apartment.getAddress())){
                apartment.setAddress(address);
            }

            if(bedrooms != apartment.getBedrooms()){
                apartment.setBedrooms(bedrooms);
            }

            if(!rentAmount.equals(apartment.getRentAmount())){
                apartment.setRentAmount(rentAmount);
            }

            if(!description.equals(apartment.getDescription())){
                apartment.setDescription(description);
            }

            assert landlordObj != null;
            if(!landlordObj.equals(apartment.getLandlord())){
                apartment.setLandlord(landlordObj);
            }

            assert districtObj != null;
            if(!districtObj.equals(apartment.getDistrict())){
                apartment.setDistrict(districtObj);
            }

            apartmentRepository.save(apartment);
        }
        return "redirect:/admin/apartments";
    }

    @GetMapping("apartment/{id}/remove")
    public String removeApartment(@PathVariable(value = "id")Long id, Model model){
        apartmentRepository.findById(id).ifPresent(apartmentRepository::delete);
        return "redirect:/admin/apartments";
    }

    @GetMapping("/admin/roles")
    public String getAllRoles(Model model){
        List<Role> roles = getAllRoles();
        model.addAttribute("roles", roles);
        return "adminRoles";
    }

    @GetMapping("/roles/newRole")
    public String getNewRole(Model model){
        return "newRole";
    }

    @PostMapping("/roles/newRole")
    public String saveNewRole(
            @RequestParam String role, Model model, RedirectAttributes redirectAttributes){
        Role roleSave = new Role();
        List<Role> roles = getAllRoles();
        boolean isExists = roles.stream().anyMatch(r -> role.equals(r.getRoleName()));
        if(!isExists){
            roleSave.setRoleName(role);
            roleRepository.save(roleSave);
        }else{
            String message = "This role '" + role + "' exists already!";
            redirectAttributes.addFlashAttribute("message", message);
        }

        return "redirect:/admin/roles";
    }

    @GetMapping("/role/{id}/update")
    public String getRoleUpdate(@PathVariable(value = "id") Long id, Model model){
        Role role = roleRepository.findById(id).orElse(null);
        if(role != null){
            model.addAttribute(role);
        }
        return "updateRole";
    }

    @PostMapping("/role/{id}/update")
    public String saveRoleUpdate(@PathVariable(value = "id") Long id,
                                 String role,
                                 Model model,
                                 RedirectAttributes redirectAttributes){
        Role roleFromDB = roleRepository.findById(id).orElse(null);
        List<Role> roles = getAllRoles();
        boolean isExists = roles.stream().anyMatch(r -> role.equals(r.getRoleName()));
        if(roleFromDB != null){
            if(!isExists){
                if(!role.equals(roleFromDB.getRoleName())){
                    roleFromDB.setRoleName(role);
                }

                roleRepository.save(roleFromDB);
            }else{
                String message = "This role '" + role + "' exists already!";
                redirectAttributes.addFlashAttribute("message", message);
            }
        }

        return "redirect:/admin/roles";
    }

    @GetMapping("/role/{id}/remove")
    public String removeRole(@PathVariable(value = "id") Long id){
        roleRepository.findById(id).ifPresent(roleRepository::delete);
        return "redirect:/admin/roles";
    }

    private List<Landlord> getAllLandlords(){
        List<Landlord> landlords = new ArrayList<>();
        landlordRepository.findAll().forEach(item -> landlords.add(item));
        return landlords;
    }

    private List<Apartment> getAllApartments(){
        List<Apartment> apartments = new ArrayList<>();
        apartmentRepository.findAll().forEach(item -> apartments.add(item));
        return apartments;
    }

    private List<District> getAllDistricts(){
        List<District> districts = new ArrayList<>();
        districtRepository.findAll().forEach(item -> districts.add(item));
        return districts;
    }

    private List<Role> getAllRoles(){
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().forEach(item -> roles.add(item));
        return roles;
    }
}