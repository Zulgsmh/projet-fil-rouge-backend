package com.itis.pfr.models;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Token {

    private String id;
    private String token;
    private LocalDateTime attributedAt;

    public Token(String token, LocalDateTime attributedAt) {
        this.token = token;
        this.attributedAt = attributedAt;
    }
}
