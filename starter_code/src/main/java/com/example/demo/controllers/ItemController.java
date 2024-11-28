package com.example.demo.controllers;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {
	private static final Logger log = LogManager.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		log.info("[GET ITEMS] Request to retrieve all items.");
		List<Item> items = itemRepository.findAll();
		log.info("[GET ITEMS] [SUCCESS] Retrieved {} items.", items.size());
		return ResponseEntity.ok(items);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		log.info("[GET ITEM BY ID] Request to retrieve item with ID: {}", id);
		return itemRepository.findById(id)
				.map(item -> {
					log.info("[GET ITEM BY ID] [SUCCESS] Retrieved item: {}", item);
					return ResponseEntity.ok(item);
				})
				.orElseGet(() -> {
					log.error("[GET ITEM BY ID] [FAIL] Item with ID: {} not found.", id);
					return ResponseEntity.notFound().build();
				});
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		log.info("[GET ITEMS BY NAME] Request to retrieve items with name: {}", name);

		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			log.error("[GET ITEMS BY NAME] [FAIL] No items found with name: {}", name);
			return ResponseEntity.notFound().build();
		} else {
			log.info("[GET ITEMS BY NAME] [SUCCESS] Retrieved {} items with name: {}", items.size(), name);
			return ResponseEntity.ok(items);
		}
	}
	
}
