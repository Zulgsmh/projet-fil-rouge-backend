package com.itis.pfr.repositories;

import com.itis.pfr.models.Container;
import com.itis.pfr.models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContainerRepository extends MongoRepository<Container, String> {

    List<Container> getContainersByUsers(Users users);

}
