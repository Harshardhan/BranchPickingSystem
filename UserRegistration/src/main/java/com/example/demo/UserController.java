package com.example.demo;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.excpetions.InValidUserException;
import com.example.demo.excpetions.UserAlreadyExistsException;
import com.example.demo.excpetions.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@PostMapping("/register")
	public ResponseEntity<User> createUser(@RequestBody @Valid User user)throws InValidUserException, UserAlreadyExistsException{
		User createdUsers = userService.createUser(user);
		logger.info("Incoming User: {}", user);
		logger.info("User registered successfully: {}", createdUsers.getId());
		return new ResponseEntity<>(createdUsers, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") Long id ,@RequestBody @Valid User updatedUser)throws UserNotFoundException{
		User usersUpdated = userService.updateUser(id, updatedUser);
		logger.info("Successfully updated users with ID {}",id);
		return  ResponseEntity.ok(usersUpdated);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id")Long id)throws UserNotFoundException{
		userService.deleteUser(id);
		logger.info("User with ID {} deleted Successfully",id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") Long id)throws UserNotFoundException{
        logger.info("🔍 Request to fetch user with ID: {}", id);
        User users = userService.getUserById(id);
		return ResponseEntity.ok(users);
	}
	
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() throws UserNotFoundException {
        logger.info("📦 Request to fetch all Users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) throws UserNotFoundException {
        String username = authentication.getName();  // comes from JWT
        User user = userService.getUserByUserName(username);
        return ResponseEntity.ok(user);
    }



}
