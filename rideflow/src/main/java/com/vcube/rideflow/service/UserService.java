
package com.vcube.rideflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User createUser(User user) {
		return userRepository.save(user);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
	}

	public User updateUser(Long id, User updatedUser) {
		User existingUser = getUserById(id);

		existingUser.setFullName(updatedUser.getFullName());
		existingUser.setEmail(updatedUser.getEmail());
		existingUser.setPhone(updatedUser.getPhone());
		existingUser.setRole(updatedUser.getRole());
		existingUser.setActive(updatedUser.getActive());

		return userRepository.save(existingUser);
	}

	public void deleteUser(Long id) {
		User existingUser = getUserById(id);
		userRepository.delete(existingUser);
	}
}
