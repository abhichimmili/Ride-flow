
package com.vcube.rideflow.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.dto.request.UserRequestDTO;
import com.vcube.rideflow.dto.response.UserResponseDTO;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.entity.enums.Role;
import com.vcube.rideflow.exception.DuplicateEmailException;
import com.vcube.rideflow.exception.DuplicatePhoneException;
import com.vcube.rideflow.exception.UserNotFoundException;
import com.vcube.rideflow.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponseDTO createUser(UserRequestDTO request) {

		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
		String phone = request.getPhone().trim();

		if (userRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("Email is already registered");
		}

		if (userRepository.existsByPhone(phone)) {
			throw new DuplicatePhoneException("Phone number is already registered");
		}

		User user = new User();
		user.setFullName(request.getFullName().trim());
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setPhone(phone);

		// Public registration always creates a CUSTOMER.
		user.setRole(Role.CUSTOMER);
		user.setActive(true);

		User savedUser = userRepository.save(user);

		return mapToResponse(savedUser);
	}

	@Transactional(readOnly = true)
	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public UserResponseDTO getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

		return mapToResponse(user);
	}

	@Transactional
	public UserResponseDTO updateUser(Long id, UserRequestDTO request) {

		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

		String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
		String phone = request.getPhone().trim();

		if (userRepository.existsByEmailAndIdNot(email, id)) {
			throw new DuplicateEmailException("Email is already registered");
		}

		if (userRepository.existsByPhoneAndIdNot(phone, id)) {
			throw new DuplicatePhoneException("Phone number is already registered");
		}

		existingUser.setFullName(request.getFullName().trim());
		existingUser.setEmail(email);
		existingUser.setPhone(phone);
		existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

		User updatedUser = userRepository.save(existingUser);

		return mapToResponse(updatedUser);
	}

	@Transactional
	public void deleteUser(Long id) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

		userRepository.delete(existingUser);
	}

	private UserResponseDTO mapToResponse(User user) {
		return new UserResponseDTO(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getRole(),
				user.getActive());
	}
}
