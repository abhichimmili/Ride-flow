
package com.vcube.rideflow.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.vcube.rideflow.dto.request.LoginRequestDTO;
import com.vcube.rideflow.dto.response.AuthResponseDTO;
import com.vcube.rideflow.dto.response.UserResponseDTO;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.repository.UserRepository;
import com.vcube.rideflow.security.JwtService;
import com.vcube.rideflow.security.RideFlowUserDetailsService;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RideFlowUserDetailsService userDetailsService;
	private final long jwtExpiration;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			RideFlowUserDetailsService userDetailsService, @Value("${jwt.expiration}") long jwtExpiration) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.jwtExpiration = jwtExpiration;
	}

	public AuthResponseDTO login(LoginRequestDTO request) {

		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

		User user = userRepository.findByEmail(email).orElseThrow(() -> invalidCredentials());

		if (!Boolean.TRUE.equals(user.getActive())
				|| !passwordEncoder.matches(request.getPassword(), user.getPassword())) {

			throw invalidCredentials();
		}

		var userDetails = userDetailsService.loadUserByUsername(email);

		String token = jwtService.generateToken(userDetails);

		UserResponseDTO userResponse = new UserResponseDTO(user.getId(), user.getFullName(), user.getEmail(),
				user.getPhone(), user.getRole(), user.getActive());
		System.out.println(">>> AuthService.login() was called");
		return new AuthResponseDTO(token, "Bearer", jwtExpiration, userResponse);
	}

	private ResponseStatusException invalidCredentials() {
		return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
	}
}
