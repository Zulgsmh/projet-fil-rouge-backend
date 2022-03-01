package com.itis.pfr.models;

import com.itis.pfr.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    @Id
    private String id;
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String country;
    private List<Roles> roles;
    private LocalDateTime createdAt;
    private List<Container> containers;

    public Users(String firstName, String lastName, String email, String password, String country, List<Roles> roles, LocalDateTime createdAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.country = country;
        this.roles = roles;
        this.createdAt = createdAt;
    }


}
