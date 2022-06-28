package com.itis.pfr.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    private String dockerContainerId;
    private String privateKey;
    private String ipAddress;

    @DBRef
    private Users users;
    private LocalDateTime createdAt;

    public Container(String name, List<String> servicesInstalled, String dns, String dockerContainerId , String privateKey, String ipAddress, Users user, LocalDateTime createdAt) {
        this.dockerContainerId = dockerContainerId;
        this.name = name;
        this.servicesInstalled = servicesInstalled;
        this.dns = dns;
        this.privateKey = privateKey;
        this.ipAddress = ipAddress;
        this.users = user;
        this.createdAt = createdAt;
    }


}
