package com.example.dz_16_spring_book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerInfo {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private int numberRooms;
    private int floor;
    private double price;
    private String district;
}
