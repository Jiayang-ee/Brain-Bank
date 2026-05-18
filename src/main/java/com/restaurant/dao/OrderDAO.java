package com.restaurant.dao;

import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(extract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query orders", e);
        }
        return orders;
    }

    public Order findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extract(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order", e);
        }
        return null;
    }

    public Order insert(Order order) {
        String sql = "INSERT INTO orders (order_no, total_amount, status) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderNo());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getStatus().name());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getLong(1));
            }
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert order", e);
        }
    }

    public boolean updateStatus(Long id, Order.OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    public List<OrderItem> findOrderItems(Long orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_item WHERE order_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("id"));
                item.setOrderId(rs.getLong("order_id"));
                item.setMenuItemId(rs.getLong("menu_item_id"));
                item.setMenuItemName(rs.getString("menu_item_name"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setQuantity(rs.getInt("quantity"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));
                items.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query order items", e);
        }
        return items;
    }

    public void insertOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_item (order_id, menu_item_id, menu_item_name, unit_price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getOrderId());
            ps.setObject(2, item.getMenuItemId());
            ps.setString(3, item.getMenuItemName());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setInt(5, item.getQuantity());
            ps.setBigDecimal(6, item.getSubtotal());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert order item", e);
        }
    }

    private Order extract(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNo(rs.getString("order_no"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }
}