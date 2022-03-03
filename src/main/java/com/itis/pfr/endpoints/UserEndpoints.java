package com.itis.pfr.endpoints;

import com.itis.pfr.models.HttpResponse;
import com.itis.pfr.models.records.UserRequestBody;
import com.itis.pfr.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserEndpoints {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<HttpResponse> getAllUsers() {
        return ResponseEntity.ok(
          HttpResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .message("users retrieved")
                  .statusCode(HttpStatus.OK.value())
                  .status(HttpStatus.OK)
                  .data(Map.of("users", userService.getAllUsers()))
                  .build()
        );
    }

    @GetMapping("/paginated/{nbPage}/{nbItemPerPage}")
    public ResponseEntity<HttpResponse> getAllUsersPaginated(@PathVariable("nbPage") int nbPage, @PathVariable("nbItemPerPage") int nbItem ) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message("users retrieved")
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .data(Map.of("users", userService.getAllUsersPaginated(nbPage, nbItem)))
                        .build()
        );
    }


    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody UserRequestBody userRequestBody) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message("user created")
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .data(Map.of("users", userService.createUser(userRequestBody)))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message("user retrieved")
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .data(Map.of("users", userService.getUserById(id)))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message("user deleted")
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .data(Map.of("users", userService.deleteUser(id)))
                        .build()
        );
    }
}
