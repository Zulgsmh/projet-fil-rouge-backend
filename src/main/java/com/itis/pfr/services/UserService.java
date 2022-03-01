package com.itis.pfr.services;

import com.itis.pfr.enums.Roles;
import com.itis.pfr.models.Users;
import com.itis.pfr.models.records.UserRequestBody;
import com.itis.pfr.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    UsersRepository usersRepository;

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Optional<Users> getUserById(String id){
        return usersRepository.findById(id);
    }

    public Users createUser(UserRequestBody userRequestBody) {
        usersRepository.findUsersByEmail(userRequestBody.email()).ifPresent((user) -> log.info("User {} already exists", user.getEmail()));
        Users users = new Users(
                userRequestBody.firstName(),
                userRequestBody.lastName(),
                userRequestBody.email(),
                userRequestBody.password(),
                userRequestBody.country(),
                List.of(Roles.STUDENT),
                LocalDateTime.now()
        );
        usersRepository.insert(users);
        return users;
    }

    public Users deleteUser(String id) {
        Optional<Users> users = usersRepository.findById(id);
        if(users.isEmpty()) throw new IllegalStateException("User does not exists");
        usersRepository.delete(users.get());
        return users.get();
    }

    public Page<Users> getAllUsersPaginated(int nbPage, int nbItem) {
        return usersRepository.findAll(
                PageRequest.of(nbPage, nbItem, Sort.by("createdAt").descending()
                        .and(Sort.by("lastName").descending())));
    }
}
