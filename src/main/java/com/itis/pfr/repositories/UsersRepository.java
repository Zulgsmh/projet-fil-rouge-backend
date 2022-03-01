package com.itis.pfr.repositories;

import com.itis.pfr.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsersRepository extends MongoRepository<Users, String> {

    Optional<Users> findUsersByEmail(String email);

}
