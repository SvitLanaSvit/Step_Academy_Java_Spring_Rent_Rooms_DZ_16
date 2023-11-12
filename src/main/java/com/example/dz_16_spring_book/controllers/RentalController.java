package com.example.dz_16_spring_book.controllers;

import com.example.dz_16_spring_book.dto.ApartmentRental;
import com.example.dz_16_spring_book.models.Apartment;
import com.example.dz_16_spring_book.models.ApartmentImage;
import com.example.dz_16_spring_book.models.District;
import com.example.dz_16_spring_book.repositories.ApartmentImageRepository;
import com.example.dz_16_spring_book.repositories.ApartmentRepository;
import com.example.dz_16_spring_book.repositories.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RentalController {
    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ApartmentImageRepository apartmentImageRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @GetMapping("/rental")
    public String getApartmentsToRental(Model model){
        List<Apartment> apartments = getAllApartments();
        List<ApartmentImage> images = getAllApartmentImages();
        List<ApartmentRental> apartmentRentals = new ArrayList<>();
        List<District> districts = getAllDistrict();

        for(var apartment : apartments){
            ApartmentRental apartmentRental = new ApartmentRental();
//            apartmentRental.setId(apartment.getId());
//            apartmentRental.setAddress(apartment.getAddress());
//            apartmentRental.setRentAmount(apartment.getRentAmount());
//            apartmentRental.setBedrooms(apartment.getBedrooms());
//            apartmentRental.setDescription(apartment.getDescription());
//            if(apartment.getDistrict() != null){
//                for (var district : districts) {
//                    if(district.getId().equals(apartment.getDistrict().getId())){
//                        apartmentRental.setDistrict(district.getNameDistrict());
//                    }
//                }
//            }
//            apartmentRental.setRented(apartment.isRented());
//            for(var image : images){
//                if(image.getLinkImage() != null){
//                    if(apartment.getId().equals(image.getApartment().getId())){
//                        apartmentRental.addToListImages(image.getLinkImage());
//                    }
//                }
//            }
            setApartmentRental(apartmentRental, apartment, districts, images);

            if(!apartment.isRented()){
                apartmentRentals.add(apartmentRental);
            }
        }

        model.addAttribute("rentals", apartmentRentals);
        return "rental";
    }

    @PostMapping("/rental/{id}/info")
    public String getApartmentInfo(@PathVariable(value = "id")Long id, Model model){
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        List<District> districts = getAllDistrict();
        List<ApartmentImage> images = getAllApartmentImages();
        ApartmentRental apartmentRental = new ApartmentRental();

        if(apartment != null){
            setApartmentRental(apartmentRental, apartment, districts, images);
        }

        model.addAttribute("rental", apartmentRental);
        return "rentalApartmentInfo";
    }

    private void setApartmentRental(
            ApartmentRental apartmentRental,
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