package com.itis.pfr.endpoints;

import com.itis.pfr.models.HttpResponse;
import com.itis.pfr.services.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("api/v1/tokens")
@AllArgsConstructor
public class TokenEndpoints {

    private final TokenService tokenService;

    @PostMapping("/{token}")
    public HttpResponse addToken(@PathVariable("token") String token){
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("token added")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("token", tokenService.addToken(token)))
                .build();
    }

    @GetMapping("/{token}")
    public HttpResponse searchToken(@PathVariable("token") String token){
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("token find")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("token", tokenService.checkIfTokenExists(token)))
                .build();
    }

    @DeleteMapping("/{token}")
    public HttpResponse deleteToken(@PathVariable("token") String token){
        return HttpResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("token deleted")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("token", tokenService.deleteToken(token)))
                .build();
    }

}
