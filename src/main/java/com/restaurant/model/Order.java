package com.restaurant.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Timestamp createdAt;

    public enum OrderStatus {
        CREATED, PAID, CANCELLED
    }

    public Order() {}

    public Order(String orderNo) {
        this.orderNo = orderNo;
        this.totalAmount = BigDecimal.ZERO;
        this.status = OrderStatus.CREATED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("订单[id=%d, 单号=%s, 总价=%.2f, 状态=%s, 时间=%s]",
                id, orderNo, totalAmount, status, createdAt);
    }
}