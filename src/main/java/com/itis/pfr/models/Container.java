package com.itis.pfr.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Container {
    @Id
    private String id;
    private String name;
    private List<String> servicesInstalled;
    private Users user;
    private LocalDateTime createdAt;

    public Container(String name, List<String> servicesInstalled, Users user, LocalDateTime createdAt) {
        this.name = name;
        this.servicesInstalled = servicesInstalled;
        this.user = user;
        this.createdAt = createdAt;
    }
}
