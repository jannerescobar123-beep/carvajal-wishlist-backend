package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.WishlistItem;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WishlistItemService {

    private final WishlistItemRepository wishlistItemRepository;

    public WishlistItemService(WishlistItemRepository wishlistItemRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
    }

    public List<WishlistItem> findAll() {
        return wishlistItemRepository.findAll();
    }

    public WishlistItem findById(Long id) {
        return wishlistItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wishlist item not found with id: " + id));
    }

    public WishlistItem create(WishlistItemDTO dto) {
        WishlistItem item = new WishlistItem();

        item.setName(dto.getName());
        item.setUrl(dto.getUrl());

        if (dto.getPrice() != null) {
            item.setPrice(BigDecimal.valueOf(dto.getPrice()));
        }

        item.setPurchased(
                dto.getPurchased() != null ? dto.getPurchased() : false
        );

        return wishlistItemRepository.save(item);
    }

    public WishlistItem update(Long id, WishlistItemDTO dto) {
        WishlistItem item = findById(id);

        item.setName(dto.getName());
        item.setUrl(dto.getUrl());

        if (dto.getPrice() != null) {
            item.setPrice(BigDecimal.valueOf(dto.getPrice()));
        } else {
            item.setPrice(null);
        }

        if (dto.getPurchased() != null) {
            item.setPurchased(dto.getPurchased());
        }

        return wishlistItemRepository.save(item);
    }

    public void delete(Long id) {
        WishlistItem item = findById(id);
        wishlistItemRepository.delete(item);
    }
}