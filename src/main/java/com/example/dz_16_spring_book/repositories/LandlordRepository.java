package com.example.dz_16_spring_book.repositories;

import com.example.dz_16_spring_book.models.Landlord;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LandlordRepository extends CrudRepository<Landlord, Long> {
    public Optional<Landlord> findByEmail(String email);
}
