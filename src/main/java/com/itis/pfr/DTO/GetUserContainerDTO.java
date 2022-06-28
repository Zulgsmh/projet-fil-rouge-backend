package com.itis.pfr.DTO;

import com.itis.pfr.models.Container;

public record GetUserContainerDTO(Container c1, com.github.dockerjava.api.model.Container c2) {}
