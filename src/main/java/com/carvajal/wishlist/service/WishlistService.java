package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.WishlistDTO;
import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.entity.Wishlist;
import com.carvajal.wishlist.entity.WishlistHistory;
import com.carvajal.wishlist.exception.ProductAlreadyInWishlistException;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.exception.StockNotAvailableException;
import com.carvajal.wishlist.repository.ProductRepository;
import com.carvajal.wishlist.repository.UserRepository;
import com.carvajal.wishlist.repository.WishlistHistoryRepository;
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
    private final WishlistHistoryRepository wishlistHistoryRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository, 
                           ProductService productService, UserRepository userRepository,
                           WishlistHistoryRepository wishlistHistoryRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.wishlistHistoryRepository = wishlistHistoryRepository;
    }

    private void recordWishlistHistory(Long userId, Product product, Integer quantity, String action) {
        wishlistHistoryRepository.save(new WishlistHistory(
                userId,
                product.getId(),
                product.getName(),
                quantity,
                product.getPrice(),
                action
        ));
    }

    @Transactional
    public WishlistDTO addToWishlist(Long userId, WishlistItemDTO wishlistItemDTO) {
        if (wishlistItemDTO.getProductId() == null) {
            throw new IllegalArgumentException("productId is required");
        }

        Product product = productRepository.findById(wishlistItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getIsActive()) {
            throw new ResourceNotFoundException("Product is not active");
        }

        if (wishlistRepository.findByUserIdAndProductId(userId, product.getId()).isPresent()) {
            throw new ProductAlreadyInWishlistException("Product already in wishlist");
        }

        productService.hasStock(product.getId(), wishlistItemDTO.getQuantity());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wishlist wishlist = new Wishlist(user, product, wishlistItemDTO.getQuantity());
        wishlist = wishlistRepository.save(wishlist);
        recordWishlistHistory(userId, product, wishlist.getQuantity(), "ADDED");

        return new WishlistDTO(product.getId(), product.getName(), wishlist.getQuantity(), product.getPrice(), true);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not in wishlist"));

        Product product = wishlist.getProduct();
        recordWishlistHistory(userId, product, wishlist.getQuantity(), "REMOVED");
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
        List<WishlistHistory> historyList = wishlistHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        
        // Extraer IDs únicos para consultar de una vez
        java.util.Set<Long> productIds = historyList.stream()
                .map(WishlistHistory::getProductId)
                .collect(Collectors.toSet());

        // Traer todos los productos en 1 sola consulta
        java.util.Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        return historyList.stream().map(history -> {
            Product product = productMap.get(history.getProductId());
            boolean inStock = product != null && product.getIsActive() && product.getStock() >= history.getQuantity();

            return new WishlistDTO(
                    history.getProductId(),
                    history.getProductName(),
                    history.getQuantity(),
                    product != null ? product.getPrice() : history.getPrice(),
                    inStock
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public WishlistDTO updateWishlistItemQuantity(Long userId, Long productId, WishlistItemDTO wishlistItemDTO) {
        if (wishlistItemDTO == null || wishlistItemDTO.getQuantity() == null || wishlistItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not in wishlist"));

        Product product = wishlist.getProduct();
        productService.hasStock(product.getId(), wishlistItemDTO.getQuantity());

        int previousQuantity = wishlist.getQuantity();
        wishlist.setQuantity(wishlistItemDTO.getQuantity());
        wishlist = wishlistRepository.save(wishlist);

        // Registrar historial real en una sola entrada indicando antes->después
        String action = String.format("UPDATED_FROM_%d_TO_%d", previousQuantity, wishlist.getQuantity());
        recordWishlistHistory(userId, product, wishlist.getQuantity(), action);

        return new WishlistDTO(
                product.getId(),
                product.getName(),
                wishlist.getQuantity(),
                product.getPrice(),
                product.getIsActive() && product.getStock() >= wishlist.getQuantity()
        );
    }
}
