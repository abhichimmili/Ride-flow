package com.vcube.rideflow.security;

import java.util.Locale;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.repository.UserRepository;

@Service
public class RideFlowUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public RideFlowUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + normalizedEmail));

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword()).authorities("ROLE_" + user.getRole().name()).disabled(!user.getActive())
				.build();
	}
}
