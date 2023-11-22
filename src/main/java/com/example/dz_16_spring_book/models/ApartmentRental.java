package com.example.dz_16_spring_book.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "apartmentRentals")
public class ApartmentRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {MERGE, PERSIST, REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
    @ManyToOne(cascade = {MERGE, PERSIST, REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", referencedColumnName = "id")
    private Landlord landlord;
    private Timestamp rentalStartDate;
    private Timestamp rentalEndDate;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Timestamp rentalDate = new Timestamp(System.currentTimeMillis());

    @PrePersist
    protected void onCreate() {
        rentalDate = new Timestamp(new Date().getTime());
    }
}