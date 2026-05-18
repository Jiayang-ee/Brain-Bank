package com.restaurant.model;

import java.sql.Timestamp;

public class InventoryItem {
    private Long id;
    private String name;
    private Integer quantity;
    private String unit;
    private Integer lowStockThreshold;
    private Timestamp updatedAt;

    public InventoryItem() {}

    public InventoryItem(String name, Integer quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.lowStockThreshold = 10;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(Integer lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isLowStock() {
        return quantity != null && lowStockThreshold != null && quantity < lowStockThreshold;
    }

    @Override
    public String toString() {
        return String.format("库存[id=%d, 名称=%s, 数量=%d%s, 预警线=%d]",
                id, name, quantity, unit, lowStockThreshold);
    }
}