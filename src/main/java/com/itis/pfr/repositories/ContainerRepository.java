package com.itis.pfr.repositories;

import com.itis.pfr.models.Container;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContainerRepository extends MongoRepository<Container, String> {
}
