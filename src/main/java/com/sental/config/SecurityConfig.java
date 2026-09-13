package com.sental.config;

import com.sental.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;

    @Value("${security.jwt.login-url}")
    private String loginUrl;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            AuthenticationManager authenticationManager,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        JwtLoginFilter loginFilter = new JwtLoginFilter(authenticationManager, jwtService, loginUrl);
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // регистрация
                        .requestMatchers(HttpMethod.POST, "/users")
                        .permitAll()
                        // login
                        .requestMatchers(HttpMethod.POST, "/auth/login")
                        .permitAll()
                        // health
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/health",
                                "/categories/health"
                        )
                        .permitAll()

                        // categories
                        .requestMatchers(
                                HttpMethod.GET,
                                "/categories/**"
                        )
                        .hasAnyRole("USER", "ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/categories/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/categories/**"
                        ) .hasRole("ADMIN")
                        .requestMatchers( HttpMethod.DELETE, "/categories/**" )
                        .hasRole("ADMIN")

                        // events
                        .requestMatchers(
                                HttpMethod.GET,
                                "/events/**"
                        )
                        .hasAnyRole("USER", "ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/events/**"
                        )
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/events/**"
                        )
                        .hasAnyRole("ORGANIZER", "ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/events/**"
                        )
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        // organizers
                        .requestMatchers(
                                HttpMethod.GET,
                                "/organizers/**"
                        ) .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/organizers/**"
                        ) .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/organizers/**" )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/organizers/**"
                        )
                        .hasRole("ADMIN")

                        // tickets
                        .requestMatchers(
                                HttpMethod.GET,
                                "/tickets/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/tickets/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/tickets/**"
                        )
                        .hasAnyRole("ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/tickets/**"
                        )
                        .hasRole("ADMIN")

                        // venues
                        .requestMatchers(
                                HttpMethod.GET,
                                "/venues/**"
                        )
                        .hasAnyRole("USER", "ORGANIZER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/venues/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/venues/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/venues/**"
                        )
                        .hasRole("ADMIN")

                        // users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        // остальное
                        .anyRequest() .authenticated()
                )
                .addFilterAt(
                        loginFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(daoAuthenticationProvider());
    }

}
