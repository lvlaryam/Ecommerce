package com.app.ecommerce.configuration.security;


import com.app.ecommerce.configuration.security.jwt.JwtService;
import com.app.ecommerce.configuration.security.jwt.filter.JwtTokenValidatorFilter;
import com.app.ecommerce.core.user.UserRepository;
import com.app.ecommerce.core.user.utils.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class AppSecurityConfig {

    private static final String ROLE_CUSTOMER = UserRole.CUSTOMER.name();
    private static final String ROLE_ADMIN = UserRole.ADMIN.name();
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer ->
                        corsCustomizer.configurationSource(
                                request -> {
                                    CorsConfiguration config = new CorsConfiguration();
                                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                                    config.setAllowedMethods(List.of("*"));
                                    config.setAllowedHeaders(Collections.singletonList("*"));
                                    config.setExposedHeaders(List.of("Authorization"));
                                    config.setAllowCredentials(true);
                                    return config;
                                }
                        )
                )
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .addFilterBefore(new JwtTokenValidatorFilter(jwtService, userRepository), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        requests -> requests
//                                .requestMatchers("/orderItem").hasAnyRole(ROLE_CUSTOMER)
//                                .requestMatchers("/order").hasAnyRole(ROLE_CUSTOMER)
//                                .requestMatchers("/product/get").hasAnyRole(ROLE_CUSTOMER)
//                                .requestMatchers("/product/").hasAnyRole(ROLE_ADMIN)
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/v2/api-docs").permitAll()
                                .requestMatchers("/swagger-resources/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .anyRequest().permitAll()

                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles(ROLE_ADMIN)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}


