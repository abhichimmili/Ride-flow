package com.vcube.rideflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vcube.rideflow.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// This is a stateless REST API using JWTs.
				.csrf(csrf -> csrf.disable())

				// Disable default browser-based authentication.
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())

				// Do not create HTTP sessions.
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

				// Configure access rules.
				.authorizeHttpRequests(auth -> auth

						// Swagger UI and OpenAPI documentation
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**")
						.permitAll()

						// Public registration and login endpoints.
						.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()

						// Only customers can create rides.
						.requestMatchers(HttpMethod.POST, "/api/rides", "/api/rides/").hasRole("CUSTOMER")

						// Only drivers can accept rides.
						.requestMatchers(HttpMethod.POST, "/api/driver/accept-ride/**").hasRole("DRIVER")

						// Only administrators can access admin endpoints.
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// Any authenticated user can access their own profile endpoint.
						.requestMatchers("/api/users/me").authenticated()

						// All other endpoints require authentication.
						.anyRequest().authenticated())

				// Read and validate JWTs before normal username/password authentication.
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}