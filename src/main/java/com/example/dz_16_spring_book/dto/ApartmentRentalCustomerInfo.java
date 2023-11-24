package com.example.dz_16_spring_book.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ApartmentRentalCustomerInfo {
    private Long id;
    private Long idLandlord;
    private String fullNameLandlord;
    private String emailLandlord;
    private String phoneLandlord;
    private Long idApartment;
    private String addressApartment;
    private Double rentAmountApartment;
    private int bedroomsApartment;
    private String descriptionApartment;
    private String districtApartment;
    String linkImagesApartment;
    Timestamp rentalDate;
    Timestamp startRentalDate;
    Timestamp endRentalDate;
}
