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
@Table(name = "apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {MERGE, PERSIST, REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", referencedColumnName = "id")
    private Landlord landlord;
    private String address;
    private Double rentAmount;
    private int bedrooms;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne(cascade = {MERGE, PERSIST, REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    private District district;
    @Column(name = "is_rented", nullable = false, columnDefinition = "boolean default false")
    private boolean isRented = false;

    @OneToMany(mappedBy = "apartment", cascade = {MERGE, PERSIST, REFRESH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ApartmentImage> apartmentImages;
    @OneToMany(mappedBy = "apartment", cascade = {MERGE, PERSIST, REFRESH},
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ApartmentRental> apartmentRentals;
}