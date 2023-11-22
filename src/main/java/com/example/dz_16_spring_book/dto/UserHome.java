package com.example.dz_16_spring_book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserHome {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String address;
}
