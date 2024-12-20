package com.example.demo.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	private Logger log = LogManager.getLogger(OrderController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("[SUBMIT ORDER] Received order submission request for user: {}", username);

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("[SUBMIT ORDER] [FAIL] User '{}' not found.", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);

		log.info("[SUBMIT ORDER] [SUCCESS] Order submitted for user: {}. Order Details: {}", username, order);
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.info("[ORDER HISTORY] Request to retrieve order history for user: {}", username);

		User user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("[ORDER HISTORY] [FAIL] User '{}' not found.", username);
			return ResponseEntity.notFound().build();
		}
		List<UserOrder> history = orderRepository.findByUser(user);

		log.info("[ORDER HISTORY] [SUCCESS] Retrieved {} orders for user: {}. Order History: {}",
				history.size(), username, history);
		return ResponseEntity.ok(history);
	}
}
