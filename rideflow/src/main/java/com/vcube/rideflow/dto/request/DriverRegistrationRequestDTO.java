
package com.vcube.rideflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DriverRegistrationRequestDTO {

	@NotBlank
	@Size(max = 100)
	private String fullName;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 8, max = 72)
	private String password;

	@NotBlank
	@Pattern(regexp = "\\d{10}")
	private String phone;

	@NotBlank
	@Size(max = 50)
	private String licenseNumber;
}
