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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String phone;
    private String address;
    private int numberRooms;
    private int floor;
    private double price;
    @ManyToOne(cascade = {MERGE, PERSIST, REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;
    @OneToMany(mappedBy = "customer", cascade = {MERGE, PERSIST, REFRESH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ApartmentRental> apartmentRentals;
    @OneToMany(mappedBy = "customer", cascade = {MERGE, PERSIST, REFRESH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<User> users;
}