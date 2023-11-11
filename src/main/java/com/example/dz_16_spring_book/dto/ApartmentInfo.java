package com.example.dz_16_spring_book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApartmentInfo {
    private Long id;
    private String landlord;
    private String address;
    private Double rentAmount;
    private int bedrooms;
    private String description;
    private String district;
    private boolean isRented;
}
