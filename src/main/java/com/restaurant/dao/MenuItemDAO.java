package com.restaurant.dao;

import com.restaurant.model.MenuItem;
import com.restaurant.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    public List<MenuItem> findAll() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_item";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(extract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query menu items", e);
        }
        return items;
    }

    public MenuItem findById(Long id) {
        String sql = "SELECT * FROM menu_item WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extract(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find menu item", e);
        }
        return null;
    }

    public MenuItem insert(MenuItem item) {
        String sql = "INSERT INTO menu_item (name, price, category, inventory_item_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setBigDecimal(2, item.getPrice());
            ps.setString(3, item.getCategory());
            ps.setObject(4, item.getInventoryItemId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getLong(1));
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert menu item", e);
        }
    }

    public boolean update(Long id, MenuItem item) {
        String sql = "UPDATE menu_item SET name = ?, price = ?, category = ?, inventory_item_id = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setBigDecimal(2, item.getPrice());
            ps.setString(3, item.getCategory());
            ps.setObject(4, item.getInventoryItemId());
            ps.setLong(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update menu item", e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM menu_item WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete menu item", e);
        }
    }

    private MenuItem extract(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem();
        item.setId(rs.getLong("id"));
        item.setName(rs.getString("name"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setCategory(rs.getString("category"));
        item.setInventoryItemId(rs.getObject("inventory_item_id") != null ? rs.getLong("inventory_item_id") : null);
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }
}