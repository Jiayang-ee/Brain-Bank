package com.restaurant.service;

import com.restaurant.dao.MenuItemDAO;
import com.restaurant.model.MenuItem;

import java.util.List;

public class MenuService {
    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    public List<MenuItem> getAllMenuItems() {
        return menuItemDAO.findAll();
    }

    public MenuItem addMenuItem(MenuItem item) {
        return menuItemDAO.insert(item);
    }

    public MenuItem updateMenuItem(Long id, MenuItem item) {
        menuItemDAO.update(id, item);
        return menuItemDAO.findById(id);
    }

    public boolean deleteMenuItem(Long id) {
        return menuItemDAO.delete(id);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemDAO.findById(id);
    }
}