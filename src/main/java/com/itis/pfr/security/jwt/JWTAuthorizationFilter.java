package com.itis.pfr.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.pfr.security.SecurityConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.JWT.decode;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private final SecurityConfiguration configuration;

	public JWTAuthorizationFilter(AuthenticationManager authManager, SecurityConfiguration configuration) {
		super(authManager);
		this.configuration = configuration;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader(SecurityConfiguration.AUTHORIZATION_HEADER);
		try {
			if (header == null || !header.startsWith(SecurityConfiguration.BEARER_PREFIX)) {
				chain.doFilter(request, response);
				return;
			}
			UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (TokenExpiredException e) {
			//check refresh token
			String refreshToken = request.getHeader("refresh_token");
			log.info(refreshToken);
			//refresh the token
			//String refreshToken = generateRefreshToken(decodedJWT.getClaims() ,decodedJWT.getSubject());
			//change header with the new value
			//response.setHeader("access_token", "Bearer "+ refreshToken);
			//chain.doFilter(request, response);
		} catch (Exception e) {
			log.info("Error in filter internal : {}", e.getMessage());
			throw e;
		}
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(SecurityConfiguration.AUTHORIZATION_HEADER);

		if (token != null) {
			String user = JWT.require(Algorithm.HMAC512(configuration.getSecret())).build()
					.verify(token.replace(SecurityConfiguration.BEARER_PREFIX, "")).getSubject();
			if (user != null) {
				String tokenToDecode = token.replace("Bearer", "").trim();
				DecodedJWT decodedJWT = decode(tokenToDecode);
				return new UsernamePasswordAuthenticationToken(user, null, getGrantedAuthorities(decodedJWT));
			}
		}
		log.info("Error logging");
		return null;
	}

	private List<SimpleGrantedAuthority> getGrantedAuthorities(DecodedJWT token) {
		return token.getClaim("userRoles").asList(String.class).stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}





}
