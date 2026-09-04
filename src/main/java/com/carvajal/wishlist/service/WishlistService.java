package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.WishlistDTO;
import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.entity.Wishlist;
import com.carvajal.wishlist.exception.ProductAlreadyInWishlistException;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.exception.StockNotAvailableException;
import com.carvajal.wishlist.repository.ProductRepository;
import com.carvajal.wishlist.repository.UserRepository;
import com.carvajal.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository, 
                           ProductService productService, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    @Transactional
    public WishlistDTO addToWishlist(Long userId, WishlistItemDTO wishlistItemDTO) {
        Product product = productRepository.findById(wishlistItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getIsActive()) {
            throw new ResourceNotFoundException("Product is not active");
        }

        if (wishlistRepository.findByUserIdAndProductId(userId, product.getId()).isPresent()) {
            throw new ProductAlreadyInWishlistException("Product already in wishlist");
        }
        
        // Throw StockNotAvailableException if not enough stock
        productService.hasStock(product.getId(), wishlistItemDTO.getQuantity());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wishlist wishlist = new Wishlist(user, product, wishlistItemDTO.getQuantity());
        wishlist = wishlistRepository.save(wishlist);

        return new WishlistDTO(product.getId(), product.getName(), wishlist.getQuantity(), product.getPrice(), true);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not in wishlist"));
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private WishlistDTO mapToDTO(Wishlist w) {
        Product p = w.getProduct();
        boolean inStock = p.getIsActive() && p.getStock() >= w.getQuantity();
        
        return new WishlistDTO(
            p.getId(), 
            p.getName(), 
            w.getQuantity(), 
            p.getPrice(), 
            inStock
        );
    }

    public List<WishlistDTO> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<WishlistDTO> getWishlistHistory(Long userId) {
        return wishlistRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<WishlistDTO> checkWishlistStock(Long userId) {
        return getWishlist(userId);
    }
}
