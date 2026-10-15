
package com.vcube.rideflow.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vcube.rideflow.dto.request.UserRequestDTO;
import com.vcube.rideflow.dto.response.UserResponseDTO;
import com.vcube.rideflow.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {

		UserResponseDTO response = userService.createUser(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {

		return ResponseEntity.ok(userService.getUserById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
			@Valid @RequestBody UserRequestDTO request) {

		return ResponseEntity.ok(userService.updateUser(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
}
