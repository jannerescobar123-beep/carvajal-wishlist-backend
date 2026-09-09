package com.carvajal.wishlist.repository;

import com.carvajal.wishlist.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByIsActiveTrue();

    List<Product> findByNameContaining(String name);
}