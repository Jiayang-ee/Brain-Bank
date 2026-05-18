package com.restaurant.service;

import com.restaurant.dao.OrderDAO;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();
    private static final AtomicInteger orderCounter = new AtomicInteger(1);

    public Order createOrder(List<OrderItem> items) {
        String orderNo = generateOrderNo();
        Order order = new Order(orderNo);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getSubtotal());
        }
        order.setTotalAmount(total);
        order.setStatus(Order.OrderStatus.CREATED);

        orderDAO.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderDAO.insertOrderItem(item);
        }

        return order;
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public Order getOrderById(Long id) {
        return orderDAO.findById(id);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderDAO.findOrderItems(orderId);
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = orderCounter.getAndIncrement();
        return String.format("ORD-%s-%03d", date, seq);
    }
}