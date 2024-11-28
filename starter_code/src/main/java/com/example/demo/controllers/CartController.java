package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private Logger log = LogManager.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		log.info("[ADD TO CART] Received request to add items to cart for user: {}", request.getUsername());

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("[ADD TO CART] [FAIL] User '{}' not found. Request: {}", request.getUsername(), request);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("[ADD TO CART] [FAIL] Item with ID '{}' not found. User: {}", request.getItemId(), request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);

		log.info("[ADD TO CART] [SUCCESS] Added {} of item '{}' to cart for user '{}'. Updated Cart: {}",
				request.getQuantity(), request.getItemId(), user.getUsername(), cart);

		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		log.info("[REMOVE FROM CART] Received request to remove items from cart for user: {}", request.getUsername());

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("[REMOVE FROM CART] [FAIL] User '{}' not found. Request: {}", request.getUsername(), request);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("[REMOVE FROM CART] [FAIL] Item with ID '{}' not found. User: {}", request.getItemId(), request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);

		log.info("[REMOVE FROM CART] [SUCCESS] Removed {} of item '{}' from cart for user '{}'. Updated Cart: {}",
				request.getQuantity(), request.getItemId(), user.getUsername(), cart);

		return ResponseEntity.ok(cart);
	}
		
}
