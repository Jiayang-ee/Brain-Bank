package com.restaurant.model;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long menuItemId;
    private String menuItemName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public OrderItem() {}

    public OrderItem(Long menuItemId, String menuItemName, BigDecimal unitPrice, Integer quantity) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return String.format("订单明细[菜品=%s, 单价=%.2f, 数量=%d, 小计=%.2f]",
                menuItemName, unitPrice, quantity, subtotal);
    }
}