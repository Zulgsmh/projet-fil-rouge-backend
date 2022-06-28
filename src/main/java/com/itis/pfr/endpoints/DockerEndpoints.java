package com.itis.pfr.endpoints;

import com.itis.pfr.models.HttpResponse;
import com.itis.pfr.services.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/docker")
public class DockerEndpoints {

    @Autowired
    DockerService dockerService;

    @GetMapping
    public HttpResponse getAllContainersOnDaemon() {
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("daemon containers fetched.")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("docker_containers", dockerService.getAllContainersOnOurDaemon()))
                .build();
    }


}
