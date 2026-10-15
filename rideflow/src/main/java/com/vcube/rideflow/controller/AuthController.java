
package com.vcube.rideflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vcube.rideflow.dto.request.LoginRequestDTO;
import com.vcube.rideflow.dto.request.UserRequestDTO;
import com.vcube.rideflow.dto.response.AuthResponseDTO;
import com.vcube.rideflow.dto.response.UserResponseDTO;
import com.vcube.rideflow.service.AuthService;
import com.vcube.rideflow.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	public AuthController(AuthService authService, UserService userService) {

		this.authService = authService;
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {

		UserResponseDTO response = userService.createUser(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

		return ResponseEntity.ok(authService.login(request));
	}
}
