package com.vcube.rideflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

	@NotBlank(message = "Full name is required")
	@Size(max = 100, message = "Name cannot exceed 100 characters")
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email address")
	@Size(max = 150)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 72, message = "Password must contain 8 to 72 characters")
	private String password;

	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "[0-9]{10}", message = "Phone number must contain 10 digits")
	private String phone;

}