package com.restaurant.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class MenuItem {
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private Long inventoryItemId;
    private Timestamp createdAt;

    public MenuItem() {}

    public MenuItem(String name, BigDecimal price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(Long inventoryItemId) { this.inventoryItemId = inventoryItemId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("菜品[id=%d, 名称=%s, 单价=%.2f, 分类=%s]",
                id, name, price, category);
    }
}