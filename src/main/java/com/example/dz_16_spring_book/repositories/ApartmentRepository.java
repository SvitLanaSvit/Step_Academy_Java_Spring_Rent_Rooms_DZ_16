package com.example.dz_16_spring_book.repositories;

import com.example.dz_16_spring_book.models.Apartment;
import org.springframework.data.repository.CrudRepository;

public interface ApartmentRepository extends CrudRepository<Apartment, Long> {
}
