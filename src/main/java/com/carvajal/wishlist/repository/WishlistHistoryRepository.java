package com.carvajal.wishlist.repository;

import com.carvajal.wishlist.entity.WishlistHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistHistoryRepository extends JpaRepository<WishlistHistory, Long> {
    List<WishlistHistory> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
