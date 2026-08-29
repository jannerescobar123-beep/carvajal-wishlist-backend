package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product create(ProductDTO dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        return productRepository.save(product);
    }

    public Product update(Long id, ProductDTO dto) {
        Product product = findById(id);

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        if (dto.getIsActive() != null) {
            product.setIsActive(dto.getIsActive());
        }

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public boolean hasStock(Long productId, int quantity) {
        Product product = findById(productId);
        return product.getStock() >= quantity;
    }
}