package com.restaurant.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static final String JDBC_URL = "jdbc:h2:mem:restaurant;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        String createMenuItem = "CREATE TABLE IF NOT EXISTS menu_item (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "category VARCHAR(50), " +
                "inventory_item_id BIGINT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String createInventory = "CREATE TABLE IF NOT EXISTS inventory_item (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "unit VARCHAR(20), " +
                "low_stock_threshold INT DEFAULT 10, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String createOrder = "CREATE TABLE IF NOT EXISTS orders (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "order_no VARCHAR(50) UNIQUE NOT NULL, " +
                "total_amount DECIMAL(10,2), " +
                "status VARCHAR(20), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String createOrderItem = "CREATE TABLE IF NOT EXISTS order_item (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "order_id BIGINT NOT NULL, " +
                "menu_item_id BIGINT, " +
                "menu_item_name VARCHAR(100), " +
                "unit_price DECIMAL(10,2), " +
                "quantity INT, " +
                "subtotal DECIMAL(10,2))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createMenuItem);
            stmt.execute(createInventory);
            stmt.execute(createOrder);
            stmt.execute(createOrderItem);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}