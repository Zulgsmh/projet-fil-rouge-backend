package com.itis.pfr.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Container {
    @Id
    private String id;
    private String name;
    private List<String> servicesInstalled;
    private String dns;
    private String privateKey;
    private String ipAddress;
    private Users user;
    private LocalDateTime createdAt;

    public Container(String name, List<String> servicesInstalled, String dns, String privateKey, String ipAddress, Users user, LocalDateTime createdAt) {
        this.name = name;
        this.servicesInstalled = servicesInstalled;
        this.dns = dns;
        this.privateKey = privateKey;
        this.ipAddress = ipAddress;
        this.user = user;
        this.createdAt = createdAt;
    }
}
