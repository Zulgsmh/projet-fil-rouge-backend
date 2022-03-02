package com.itis.pfr.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.pfr.models.HttpResponse;
import com.itis.pfr.models.Users;
import com.itis.pfr.security.SecurityConfiguration;
import com.itis.pfr.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(path = "/api/v1/refreshJwt")
public class JWTEndpoints {

    @Autowired
    SecurityConfiguration configuration;

    @Autowired
    UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse> refreshJwt(@RequestBody JWTRefresh refreshBody){
        try {
            if(refreshBody.access_token().equals("")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            DecodedJWT decodedJWT = JWT.decode(refreshBody.access_token());
            //compare with actual date and time
            Timestamp timestamp1 = new Timestamp(new Date(System.currentTimeMillis() + SecurityConfiguration.EXPIRATION_TIME_REFRESH).getTime());
            Timestamp timestamp2 = new Timestamp(decodedJWT.getExpiresAt().getTime());
            if( timestamp1.compareTo(timestamp2) < 0 ){
                //refresh is expired too => user need to log in again
                throw new JWTVerificationException("Session expired please log in again");
            }

            //get the user
            Optional<Users> user = userService.getUserById(refreshBody.idEmployee());

            if(user.isEmpty()) throw new IllegalStateException("The user does not exists");

            String[] roles= new String[user.get().getRoles().size()];

            for(int i=0; i<user.get().getRoles().size(); i++)
            {
                roles[i] = user.get().getRoles().get(i).toString();
            }

            String token = JWT.create().withSubject(user.get().getEmail())
                    .withClaim("id", user.get().getId())
                    .withClaim("firstName", user.get().getFirstName())
                    .withClaim("lastName", user.get().getLastName())
                    .withClaim("email", user.get().getEmail())
                    .withArrayClaim("userRoles", roles)
                    .withExpiresAt(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))
                    .withIssuedAt(Date.from(Instant.now()))
                    .withIssuer("auth0")
                    .sign(Algorithm.HMAC512(configuration.getSecret()));

            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .data(Map.of("access_token", token))
                            .message("Authentication successfully refreshed")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value()).build()
            );
        } catch (Exception e) {
            log.info("Error while refreshing jwt : {}", e.getMessage());
            return ResponseEntity.ok(
                    HttpResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .message(e.getMessage())
                            .status(HttpStatus.UNAUTHORIZED)
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

}
