package com.shary.auth.config;

import com.shary.auth.config.filter.JwtRequestFilter;
import com.shary.auth.repository.entity.user.support.RolesNames;
import com.shary.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService service;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;
    private final String GUEST = RolesNames.GUEST.name();
    private final String OWNER = RolesNames.OWNER.name();
    private final String RENTER = RolesNames.RENTER.name();
    private final String DBA = RolesNames.DBA.name();
    private final String ADMIN = RolesNames.ADMIN.name();
    private final String MODERATOR = RolesNames.MODERATOR.name();

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(service)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests(authorize -> {
                    try {
                        authorize
                                .requestMatchers("/users/profile")
                                .authenticated()
                                .requestMatchers("/users/guest")
                                .permitAll()
                                .requestMatchers("/users/renter")
                                .authenticated()
                                .requestMatchers("/users/owner")
                                .authenticated()
                                .requestMatchers("/users/roles/**")
                                .hasAnyRole(ADMIN, DBA)
                                .requestMatchers("/users")
                                .hasAnyRole(DBA, ADMIN, MODERATOR)
                                .requestMatchers("/users/admin/**")
                                .hasAnyRole(DBA, ADMIN, MODERATOR)
                                .requestMatchers("/users/{userId}")
                                .hasAnyRole(DBA, ADMIN, MODERATOR)
                                .requestMatchers("/users/admin/moderators")
                                .hasAnyRole(ADMIN, DBA)
                                .requestMatchers("/users/admin/admins")
                                .hasAnyRole(DBA)

                                .requestMatchers("/core", "/login", "/core/about")
                                .permitAll()
                                .requestMatchers("/core", "/login", "/about")
                                .anonymous()
                                .requestMatchers("/core/items/**")
                                .hasAnyRole(ADMIN, MODERATOR, DBA, OWNER, RENTER, GUEST)
                                .requestMatchers("/core/admin/**")
                                .hasAnyRole(ADMIN, MODERATOR, DBA)
                                .requestMatchers("/core/admin/moderators")
                                .hasAnyRole(ADMIN, DBA)
                                .requestMatchers("/core/admin/admins")
                                .hasRole(DBA)
                                .requestMatchers("/core/db/**")
                                .access(AuthorizationManagers
                                        .allOf(AuthorityAuthorizationManager
                                                        .hasRole(ADMIN),
                                                AuthorityAuthorizationManager.hasRole(DBA)))
                                .requestMatchers(HttpMethod.DELETE).hasRole(ADMIN)
                                .anyRequest()
                                .authenticated()
                                .and()
                                .httpBasic()
                                .and()
                                .sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                                .and()
//                                .headers().frameOptions().disable()
                                .and()
                                .exceptionHandling()
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
