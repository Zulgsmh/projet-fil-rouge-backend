package com.itis.pfr.services;

import com.itis.pfr.models.Token;
import com.itis.pfr.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public Optional<Token> findToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public boolean checkIfTokenExists(String token) {
        return findToken(token).isPresent();
    }

    public String deleteToken(String token) {
        tokenRepository.findByToken(token).ifPresent(tokenRepository::delete);
        return token;
    }

    public Token addToken(String token) {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if(optionalToken.isPresent()) throw new IllegalStateException("Token already exists");
        Token newToken = new Token(token, LocalDateTime.now());
        tokenRepository.insert(newToken);
        return newToken;
    }

}
