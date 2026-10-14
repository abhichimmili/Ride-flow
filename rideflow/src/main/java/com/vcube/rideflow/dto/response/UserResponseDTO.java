package com.vcube.rideflow.dto.response;

import com.vcube.rideflow.entity.enums.Role;

import lombok.Data;

@Data
public class UserResponseDTO {
	
	private Long id;
	private String fullName;
	private String email;
	private String phone;
	private Role role;
	private boolean active;

	public UserResponseDTO(Long id, String fullName, String email, String phone, Role role, boolean active) {
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.role = role;
		this.active = active;
	}

}