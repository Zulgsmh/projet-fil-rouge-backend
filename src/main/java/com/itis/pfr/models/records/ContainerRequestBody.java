package com.itis.pfr.models.records;

import com.itis.pfr.DTO.CreateContainerDTO;

import java.util.List;

public record ContainerRequestBody(
        String name,
        List<String> servicesInstalled,
        String userId) {
}
