package com.itis.pfr.security;

import com.itis.pfr.security.jwt.JWTAuthenticationFilter;
import com.itis.pfr.security.jwt.JWTAuthorizationFilter;
import com.itis.pfr.services.TokenService;
import com.itis.pfr.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Value("${application.security.token.secret}")
    private String secret;

    @Value("${application.security.refreshToken.secret}")
    private String refreshSecret;



    /** Token configuration */
    public static final long EXPIRATION_TIME_REFRESH = 10 * 60 * 1000 * 12 + 1800000; //2h30 => 9_000_000
    public static final long EXPIRATION_TIME = 10 * 60 * 1000 * 6 ;//12 ; //600_000; // 10 mins => 2h = 7_200_000
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";


    /** Endpoints security configuration */
    private static final String API_BASE = "/api/v*";
    private static final String USER_ENDPOINTS = API_BASE + "/users/**";
    private static final String CONTAINER_ENDPOINTS = API_BASE + "/containers/**";

    private static final String REFRESH_ENPOINT = API_BASE + "/refreshJwt";


    public String getSecret() {
        return secret;
    }

    public String getRefreshSecret() { return refreshSecret; }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"));
        http.cors().and().csrf().disable().authorizeRequests()
                /** Refresh token */
                .antMatchers(HttpMethod.POST, REFRESH_ENPOINT).hasAnyAuthority("STUDENT", "ADMIN")

                /** Container API */
                .antMatchers(HttpMethod.GET, CONTAINER_ENDPOINTS).hasAnyAuthority("ADMIN", "STUDENT")
                .antMatchers(HttpMethod.POST, CONTAINER_ENDPOINTS).hasAnyAuthority("ADMIN", "STUDENT")
                .antMatchers(HttpMethod.DELETE, CONTAINER_ENDPOINTS).hasAnyAuthority("ADMIN", "STUDENT")

                /** User API */
                .antMatchers(HttpMethod.DELETE, USER_ENDPOINTS).hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, USER_ENDPOINTS).hasAnyAuthority("ADMIN", "STUDENT")
                .antMatchers(HttpMethod.POST, USER_ENDPOINTS).permitAll()
                .antMatchers(API_BASE)
                .authenticated().and().addFilter(getJWTAuthenticationFilter())
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), this)).sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedMethod("DELETE");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    private JWTAuthenticationFilter getJWTAuthenticationFilter() throws Exception {
        JWTAuthenticationFilter filter = new JWTAuthenticationFilter(authenticationManager(), this);
        filter.setFilterProcessesUrl("/api/v*/auth/login");
        return filter;
    }


}
