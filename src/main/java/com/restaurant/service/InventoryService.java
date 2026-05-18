package com.restaurant.service;

import com.restaurant.dao.InventoryDAO;
import com.restaurant.model.InventoryItem;

import java.util.List;

public class InventoryService {
    private InventoryDAO inventoryDAO = new InventoryDAO();

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryDAO.findAll();
    }

    public InventoryItem addInventoryItem(InventoryItem item) {
        return inventoryDAO.insert(item);
    }

    public boolean deductInventory(Long menuItemId, Integer quantity) {
        InventoryItem item = inventoryDAO.findById(menuItemId);
        if (item == null) return false;
        int newQuantity = item.getQuantity() - quantity;
        if (newQuantity < 0) newQuantity = 0;
        return inventoryDAO.updateQuantity(menuItemId, newQuantity);
    }

    public List<InventoryItem> getLowStockAlerts() {
        return inventoryDAO.findLowStock();
    }
}