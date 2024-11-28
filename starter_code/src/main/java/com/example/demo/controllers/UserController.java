package com.example.demo.controllers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private Logger log = LogManager.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		log.info("[FIND USER BY ID] Request to find user with ID: {}", id);

		return userRepository.findById(id)
				.map(user -> {
					log.info("[FIND USER BY ID] [SUCCESS] User found: {}", user);
					return ResponseEntity.ok(user);
				})
				.orElseGet(() -> {
					log.error("[FIND USER BY ID] [FAIL] User with ID: {} not found.", id);
					return ResponseEntity.notFound().build();
				});
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.info("[FIND USER BY USERNAME] Request to find user with username: {}", username);

		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.error("[FIND USER BY USERNAME] [FAIL] User '{}' not found.", username);
			return ResponseEntity.notFound().build();
		}
		log.info("[FIND USER BY USERNAME] [SUCCESS] User found: {}", user);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.info("[CREATE USER] Request to create user with username: {}", createUserRequest.getUsername());

		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();

		cartRepository.save(cart);
		user.setCart(cart);

		if(createUserRequest.getPassword().length() < 7 ){
			log.error("[CREATE USER] [FAIL] Username: {}, Reason: Password too short (less than 7 characters).",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().body("Password must be at least 7 characters.");
		}else if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.error("[CREATE USER] [FAIL] Username: {}, Reason: Password and confirm password do not match.",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().body("Password field does not match confirm password field");
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		userRepository.save(user);

		log.info("[CREATE USER] [SUCCESS] User created: {}", user);

		return ResponseEntity.ok(user);
	}
	
}
