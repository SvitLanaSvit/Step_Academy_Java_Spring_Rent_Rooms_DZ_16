package com.example.dz_16_spring_book.repositories;

import com.example.dz_16_spring_book.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
}
