package com.example.dz_16_spring_book.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "landlords")
public class Landlord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    @OneToMany(mappedBy = "landlord", cascade = {MERGE, PERSIST, REFRESH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Apartment> apartments;

    @OneToMany(mappedBy = "landlord", cascade = {MERGE, PERSIST, REFRESH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ApartmentRental> apartmentRentals;
}