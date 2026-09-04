package com.carvajal.wishlist.dto;

import java.math.BigDecimal;

public class WishlistDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private Boolean inStock;

    public WishlistDTO() {}

    public WishlistDTO(Long productId, String productName, Integer quantity, BigDecimal price, Boolean inStock) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.inStock = inStock;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
}
