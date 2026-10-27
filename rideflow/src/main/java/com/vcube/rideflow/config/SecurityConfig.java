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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.vcube.rideflow.security.JwtAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// Stateless REST API using JWTs
				.csrf(csrf -> csrf.disable())

				// Enable CORS through Spring Security
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				// Disable default browser-based authentication
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())

				// Do not create HTTP sessions
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

				// Authorization rules
				.authorizeHttpRequests(auth -> auth

						// Swagger UI and OpenAPI documentation
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**")
						.permitAll()

						// Public registration and login
						.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/register-driver",
								"/api/auth/login")
						.permitAll()

						// Only customers can create rides
						.requestMatchers(HttpMethod.POST, "/api/rides", "/api/rides/").hasRole("CUSTOMER")

						.requestMatchers(HttpMethod.GET, "/api/rides/**").hasRole("CUSTOMER")

						.requestMatchers(HttpMethod.PATCH, "/api/rides/*/cancel").hasRole("CUSTOMER")

						// Only drivers can accept rides
						.requestMatchers(HttpMethod.POST, "/api/driver/accept-ride/**").hasRole("DRIVER")

						// Only administrators can access admin endpoints
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// Authenticated users can access their profile endpoint
						.requestMatchers("/api/users/me").authenticated()

						// Driver profile creation
						.requestMatchers(HttpMethod.POST, "/api/driver-profiles").hasRole("DRIVER")

						// Driver profile updates
						.requestMatchers(HttpMethod.PUT, "/api/driver-profiles/**").hasAnyRole("DRIVER", "ADMIN")

						// Only admins can delete driver profiles
						.requestMatchers(HttpMethod.DELETE, "/api/driver-profiles/**").hasRole("ADMIN")

						// Only drivers can register vehicles
						.requestMatchers(HttpMethod.POST, "/api/vehicles").hasRole("DRIVER")

						// Vehicle updates
						.requestMatchers(HttpMethod.PUT, "/api/vehicles/**").hasAnyRole("DRIVER", "ADMIN")

						// Only admins can delete vehicles
						.requestMatchers(HttpMethod.DELETE, "/api/vehicles/**").hasRole("ADMIN")
						
						// All other endpoints require authentication
						.anyRequest().authenticated())

				// Validate JWTs before standard authentication processing
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:5173"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		// Bearer JWT authentication does not require cross-origin cookies
		configuration.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}