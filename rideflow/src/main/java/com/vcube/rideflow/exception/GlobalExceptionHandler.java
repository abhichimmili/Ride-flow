package com.vcube.rideflow.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(VehicleNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleVehicleNotFound(VehicleNotFoundException ex) {

		return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(DriverProfileNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleDriverNotFound(DriverProfileNotFoundException ex) {

		return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(DuplicateVehicleException.class)
	public ResponseEntity<Map<String, Object>> handleDuplicateVehicle(DuplicateVehicleException ex) {

		return buildError(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {

		Map<String, Object> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("message", "Validation failed");
		body.put("errors", errors);

		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {

		return buildError(HttpStatus.CONFLICT, "The operation violates a database constraint");
	}

	private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {

		Map<String, Object> body = new HashMap<>();

		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);

		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {

		return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler({ DuplicateEmailException.class, DuplicatePhoneException.class, DuplicateLicenseException.class,
			DriverProfileAlreadyExistsException.class })
	public ResponseEntity<Map<String, Object>> handleDuplicate(RuntimeException ex) {

		return buildError(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(InvalidUserRoleException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidUserRole(InvalidUserRoleException ex) {

		return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {

		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

		Map<String, Object> response = Map.of("status", status.value(), "message",
				ex.getReason() != null ? ex.getReason() : status.getReasonPhrase());

		return ResponseEntity.status(status).body(response);
	}
}
