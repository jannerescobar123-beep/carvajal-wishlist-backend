package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.WishlistItem;
import com.carvajal.wishlist.service.WishlistItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistItemController {

    private final WishlistItemService wishlistItemService;

    public WishlistItemController(WishlistItemService wishlistItemService) {
        this.wishlistItemService = wishlistItemService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItem>> findAll() {
        return ResponseEntity.ok(wishlistItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistItem> findById(@PathVariable Long id) {
        return ResponseEntity.ok(wishlistItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<WishlistItem> create(
            @Valid @RequestBody WishlistItemDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistItemService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WishlistItem> update(
            @PathVariable Long id,
            @Valid @RequestBody WishlistItemDTO dto) {

        return ResponseEntity.ok(
                wishlistItemService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wishlistItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}