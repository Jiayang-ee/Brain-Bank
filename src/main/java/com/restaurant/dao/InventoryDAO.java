package com.restaurant.dao;

import com.restaurant.model.InventoryItem;
import com.restaurant.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public List<InventoryItem> findAll() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_item";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(extract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query inventory items", e);
        }
        return items;
    }

    public InventoryItem findById(Long id) {
        String sql = "SELECT * FROM inventory_item WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extract(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find inventory item", e);
        }
        return null;
    }

    public InventoryItem insert(InventoryItem item) {
        String sql = "INSERT INTO inventory_item (name, quantity, unit, low_stock_threshold) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setString(3, item.getUnit());
            ps.setInt(4, item.getLowStockThreshold() != null ? item.getLowStockThreshold() : 10);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getLong(1));
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert inventory item", e);
        }
    }

    public boolean updateQuantity(Long id, Integer quantity) {
        String sql = "UPDATE inventory_item SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update inventory quantity", e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM inventory_item WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete inventory item", e);
        }
    }

    public List<InventoryItem> findLowStock() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_item WHERE quantity < low_stock_threshold";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(extract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query low stock items", e);
        }
        return items;
    }

    private InventoryItem extract(ResultSet rs) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setId(rs.getLong("id"));
        item.setName(rs.getString("name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnit(rs.getString("unit"));
        item.setLowStockThreshold(rs.getInt("low_stock_threshold"));
        item.setUpdatedAt(rs.getTimestamp("updated_at"));
        return item;
    }
}