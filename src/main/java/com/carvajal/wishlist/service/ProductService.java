package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.exception.StockNotAvailableException;
import com.carvajal.wishlist.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> findAll() {
        return productRepository.findAllByIsActiveTrue()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::getIsActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        return toDTO(product);
    }

    public ProductDTO create(ProductDTO dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setIsActive(
                dto.getIsActive() != null ? dto.getIsActive() : true
        );

        return toDTO(productRepository.save(product));
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        if (dto.getIsActive() != null) {
            product.setIsActive(dto.getIsActive());
        }

        return toDTO(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }

    public boolean hasStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .filter(Product::getIsActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        if (product.getStock() < quantity) {
            throw new StockNotAvailableException(
                    "Stock not available for product with id: " + productId
            );
        }

        return true;
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setIsActive(product.getIsActive());

        return dto;
    }
}