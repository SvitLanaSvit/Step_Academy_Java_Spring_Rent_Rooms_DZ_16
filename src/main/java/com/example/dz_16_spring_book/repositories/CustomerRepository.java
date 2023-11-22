package com.example.dz_16_spring_book.repositories;

import com.example.dz_16_spring_book.models.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    public Optional<Customer> findByEmail(String email);
}
