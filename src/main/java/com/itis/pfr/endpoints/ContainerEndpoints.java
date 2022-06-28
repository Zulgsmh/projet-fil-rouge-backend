package com.itis.pfr.endpoints;

import com.itis.pfr.models.HttpResponse;
import com.itis.pfr.models.records.ContainerRequestBody;
import com.itis.pfr.services.ContainerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("api/v1/containers")
public class ContainerEndpoints {

    private final ContainerService containerService;

    public ContainerEndpoints(ContainerService containerService) {
        this.containerService = containerService;
    }

    @PostMapping("/start")
    public HttpResponse startContainer(@RequestParam("containerId") String containerId, @RequestParam("userId") String userId) {
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("container created")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("container", containerService.startContainer(containerId, userId)))
                .build();
    }

    @PostMapping("/stop")
    public HttpResponse stopContainer(@RequestParam("containerId") String containerId, @RequestParam("userId") String userId) {
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("container created")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("container", containerService.startContainer(containerId, userId)))
                .build();
    }

    @PostMapping
    public HttpResponse createContainer(@RequestBody ContainerRequestBody containerRequestBody) {
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("container created")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("container", containerService.createContainer(containerRequestBody)))
                .build();
    }

    @GetMapping("/user/{userId}")
    public HttpResponse getAllContainerOfUser(@PathVariable("userId") String userId) {
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("successfully fetched containers of user")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("containers", containerService.getAllContainersOfUser(userId)))
                .build();
    }

    @GetMapping("/container/{containerId}")
    public HttpResponse getContainerById(@PathVariable("containerId") String id){
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("successfully fetched containers")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("containers", containerService.getContainerById(id)))
                .build();
    }

    @DeleteMapping
    public HttpResponse deleteContainer(@RequestParam("containerId") String containerId,
                                        @RequestParam("userId") String userId,
                                        @RequestParam("dockerContainerId") String dockerContainerId){
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("container deleted")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("container", containerService.deleteContainer(userId, containerId, dockerContainerId)))
                .build();
    }

}
