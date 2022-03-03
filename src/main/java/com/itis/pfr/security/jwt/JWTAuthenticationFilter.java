package com.itis.pfr.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itis.pfr.models.Users;
import com.itis.pfr.security.SecurityConfiguration;
import com.itis.pfr.services.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@AllArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final TokenService tokenService;
	private AuthenticationManager authenticationManager;
	private SecurityConfiguration configuration;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			Users userCreds = new ObjectMapper().readValue(request.getInputStream(), Users.class);
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userCreds.getUsername(),
					userCreds.getPassword(), new ArrayList<>()));
		} catch (IOException e) {
			log.info("authentication rejected :  {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		Users user = (Users) authResult.getPrincipal();

		String[] roles= new String[user.getRoles().size()];

		for(int i=0; i<user.getRoles().size(); i++)
		{
			roles[i] = user.getRoles().get(i).toString();
		}

		String token = JWT.create().withSubject(user.getEmail())
				.withClaim("id", user.getId())
				.withClaim("firstName", user.getFirstName())
				.withClaim("lastName", user.getLastName())
				.withClaim("email", user.getEmail())
				.withArrayClaim("userRoles", roles)
				.withExpiresAt(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))
				.withIssuedAt(Date.from(Instant.now()))
				.withIssuer("auth0")
				.withIssuer(request.getRequestURL().toString())
				.sign(Algorithm.HMAC512(configuration.getSecret()));

		//save token in db
		tokenService.addToken(token);

		response.setHeader("access_token", token);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(token);
		response.getWriter().flush();
	}


}
