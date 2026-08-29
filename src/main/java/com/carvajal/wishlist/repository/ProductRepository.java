package com.carvajal.wishlist.repository;

import com.carvajal.wishlist.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}