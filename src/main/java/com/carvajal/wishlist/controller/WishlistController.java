package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.WishlistDTO;
import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.repository.UserRepository;
import com.carvajal.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    public WishlistController(WishlistService wishlistService, UserRepository userRepository) {
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }

    private Long getAuthenticatedUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return user.getId();
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<WishlistDTO>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist(getAuthenticatedUserId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<WishlistDTO> addToWishlist(@Valid @RequestBody WishlistItemDTO wishlistItemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.addToWishlist(getAuthenticatedUserId(), wishlistItemDTO));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(getAuthenticatedUserId(), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<WishlistDTO>> getWishlistHistory() {
        return ResponseEntity.ok(wishlistService.getWishlistHistory(getAuthenticatedUserId()));
    }
}
