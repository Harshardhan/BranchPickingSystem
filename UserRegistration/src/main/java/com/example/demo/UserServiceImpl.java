package com.example.demo;

import java.util.List;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.excpetions.InValidUserException;
import com.example.demo.excpetions.UserAlreadyExistsException;
import com.example.demo.excpetions.UserNotFoundException;

import jakarta.transaction.Transactional;



@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User createUser(User user) throws InValidUserException, UserAlreadyExistsException {
		if (user == null) {
			throw new InValidUserException("Invalid user details.");
		}

		if (userRepository.findByUserName(user.getUserName()).isPresent()) {
			throw new UserAlreadyExistsException("User with username " + user.getUserName() + " already exists.");
		}

		if (userRepository.findByEmailId(user.getEmailId()).isPresent()) {
			throw new UserAlreadyExistsException("User with email " + user.getEmailId() + " already exists.");
		}

		if (user.getPassword() == null || user.getPassword().isBlank()) {
			throw new InValidUserException("Password cannot be null or blank");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		logger.info("Successfully registered new user: {}", savedUser.getUserName());
		return savedUser;
	}

	@Override
	public User updateUser(Long id, User updatedUser) throws UserNotFoundException {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

		if (updatedUser.getUserName() != null) {
			existingUser.setUserName(updatedUser.getUserName());
		}
		if (updatedUser.getFirstName() != null) {
			existingUser.setFirstName(updatedUser.getFirstName());
		}
		if (updatedUser.getLastName() != null) {
			existingUser.setLastName(updatedUser.getLastName());
		}
		if (updatedUser.getEmailId() != null) {
			existingUser.setEmailId(updatedUser.getEmailId());
		}
		if (updatedUser.getAddress() != null) {
			existingUser.setAddress(updatedUser.getAddress());
		}
		if (updatedUser.getMobileNumber() != null) {
			existingUser.setMobileNumber(updatedUser.getMobileNumber());
		}
		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
			existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}
		if (updatedUser.getRoles() != null) {
			existingUser.setRoles(updatedUser.getRoles());
		}

		User savedUser = userRepository.save(existingUser);
		logger.info("User with ID {} updated successfully", id);
		return savedUser;
	}

	@Override
	public void deleteUser(Long id) throws UserNotFoundException {
		Optional<User> deletedUser = userRepository.findById(id);

		if (deletedUser.isEmpty()) {
			logger.error("Attempted to delete non-existent user with id {}", id);
			throw new UserNotFoundException("User with id " + id + " not found");
		}

		userRepository.deleteById(id);
		logger.info("Successfully deleted user with ID {}", id);
	}

	@Override
	public User getUserById(Long id) throws UserNotFoundException {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = userRepository.findAll();
		logger.info("Successfully retrieved {} users", users.size());
		return users;
	}
	@Override
	public User getUserByUserName(String username) throws UserNotFoundException {
	    return userRepository.findByUserName(username)
	            .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
	}

}
