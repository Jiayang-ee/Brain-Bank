package com.restaurant.ui;

import com.restaurant.model.*;
import com.restaurant.service.*;
import com.restaurant.util.DBUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainUI {
    private MenuService menuService = new MenuService();
    private InventoryService inventoryService = new InventoryService();
    private OrderService orderService = new OrderService();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DBUtil.initDatabase();
        MainUI ui = new MainUI();
        ui.run();
    }

    public void run() {
        System.out.println("========================================");
        System.out.println("  餐饮店收银与库存管理系统");
        System.out.println("========================================");

        while (true) {
            showMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": manageMenu(); break;
                case "2": manageInventory(); break;
                case "3": posOrder(); break;
                case "4": viewOrders(); break;
                case "0": System.out.println("感谢使用，再见！"); return;
                default: System.out.println("无效选项，请重新选择。");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n--- 主菜单 ---");
        System.out.println("1. 菜单管理");
        System.out.println("2. 库存管理");
        System.out.println("3. 收银点餐");
        System.out.println("4. 订单查询");
        System.out.println("0. 退出");
        System.out.print("请选择: ");
    }

    private void manageMenu() {
        while (true) {
            System.out.println("\n--- 菜单管理 ---");
            System.out.println("1. 查看菜单");
            System.out.println("2. 添加菜品");
            System.out.println("0. 返回");
            System.out.print("请选择: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": listMenu(); break;
                case "2": addMenuItem(); break;
                case "0": return;
                default: System.out.println("无效选项。");
            }
        }
    }

    private void listMenu() {
        List<MenuItem> items = menuService.getAllMenuItems();
        if (items.isEmpty()) {
            System.out.println("菜单为空。");
            return;
        }
        System.out.println("\n--- 菜单列表 ---");
        System.out.printf("%-5s %-15s %-10s %-10s%n", "ID", "名称", "单价", "分类");
        System.out.println("-------------------------------------");
        for (MenuItem item : items) {
            System.out.printf("%-5d %-15s %-10.2f %-10s%n",
                    item.getId(), item.getName(), item.getPrice(), item.getCategory());
        }
    }

    private void addMenuItem() {
        try {
            System.out.print("请输入菜品名称: ");
            String name = scanner.nextLine().trim();
            System.out.print("请输入单价: ");
            BigDecimal price = new BigDecimal(scanner.nextLine().trim());
            System.out.print("请输入分类: ");
            String category = scanner.nextLine().trim();

            MenuItem item = new MenuItem(name, price, category);
            menuService.addMenuItem(item);
            System.out.println("添加成功！" + item);
        } catch (Exception e) {
            System.out.println("添加失败: " + e.getMessage());
        }
    }

    private void manageInventory() {
        while (true) {
            System.out.println("\n--- 库存管理 ---");
            System.out.println("1. 查看库存");
            System.out.println("2. 入库");
            System.out.println("3. 低库存预警");
            System.out.println("0. 返回");
            System.out.print("请选择: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": listInventory(); break;
                case "2": addInventory(); break;
                case "3": lowStockAlert(); break;
                case "0": return;
                default: System.out.println("无效选项。");
            }
        }
    }

    private void listInventory() {
        List<InventoryItem> items = inventoryService.getAllInventoryItems();
        if (items.isEmpty()) {
            System.out.println("库存为空。");
            return;
        }
        System.out.println("\n--- 库存列表 ---");
        System.out.printf("%-5s %-15s %-10s %-10s %-10s%n", "ID", "名称", "数量", "单位", "预警线");
        System.out.println("--------------------------------------------");
        for (InventoryItem item : items) {
            String lowStockFlag = item.isLowStock() ? " [低库存]" : "";
            System.out.printf("%-5d %-15s %-10d %-10s %-10d%s%n",
                    item.getId(), item.getName(), item.getQuantity(), item.getUnit(),
                    item.getLowStockThreshold(), lowStockFlag);
        }
    }

    private void addInventory() {
        try {
            System.out.print("请输入原料名称: ");
            String name = scanner.nextLine().trim();
            System.out.print("请输入数量: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("请输入单位: ");
            String unit = scanner.nextLine().trim();

            InventoryItem item = new InventoryItem(name, quantity, unit);
            inventoryService.addInventoryItem(item);
            System.out.println("入库成功！" + item);
        } catch (Exception e) {
            System.out.println("入库失败: " + e.getMessage());
        }
    }

    private void lowStockAlert() {
        List<InventoryItem> items = inventoryService.getLowStockAlerts();
        if (items.isEmpty()) {
            System.out.println("所有库存充足，无预警。");
            return;
        }
        System.out.println("\n--- 低库存预警 ---");
        System.out.printf("%-5s %-15s %-10s %-10s%n", "ID", "名称", "当前数量", "预警线");
        System.out.println("-------------------------------------");
        for (InventoryItem item : items) {
            System.out.printf("%-5d %-15s %-10d %-10d%n",
                    item.getId(), item.getName(), item.getQuantity(), item.getLowStockThreshold());
        }
    }

    private void posOrder() {
        List<MenuItem> menu = menuService.getAllMenuItems();
        if (menu.isEmpty()) {
            System.out.println("菜单为空，请先添加菜品。");
            return;
        }

        System.out.println("\n--- 收银点餐 ---");
        listMenu();

        List<OrderItem> orderItems = new ArrayList<>();

        while (true) {
            System.out.print("\n请输入菜品ID（输入0完成点餐）: ");
            String input = scanner.nextLine().trim();
            int menuItemId = Integer.parseInt(input);
            if (menuItemId == 0) break;

            MenuItem selected = menu.stream().filter(m -> m.getId().longValue() == menuItemId).findFirst().orElse(null);
            if (selected == null) {
                System.out.println("无效的菜品ID。");
                continue;
            }

            System.out.print("请输入数量: ");
            int qty = Integer.parseInt(scanner.nextLine().trim());

            orderItems.add(new OrderItem(selected.getId(), selected.getName(), selected.getPrice(), qty));
            System.out.println("已添加: " + selected.getName() + " x " + qty);
        }

        if (orderItems.isEmpty()) {
            System.out.println("未选择任何菜品。");
            return;
        }

        Order order = orderService.createOrder(orderItems);
        System.out.println("\n=== 订单已生成 ===");
        System.out.println("订单号: " + order.getOrderNo());
        System.out.println("总价: " + order.getTotalAmount() + " 元");
        System.out.println("状态: " + order.getStatus());
    }

    private void viewOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("暂无订单。");
            return;
        }

        System.out.println("\n--- 订单列表 ---");
        System.out.printf("%-5s %-20s %-10s %-10s%n", "ID", "订单号", "总价", "状态");
        System.out.println("-------------------------------------");
        for (Order order : orders) {
            System.out.printf("%-5d %-20s %-10.2f %-10s%n",
                    order.getId(), order.getOrderNo(), order.getTotalAmount(), order.getStatus());
        }

        System.out.print("\n请输入订单ID查看详情（输入0返回）: ");
        String input = scanner.nextLine().trim();
        int orderId = Integer.parseInt(input);
        if (orderId == 0) return;

        Order order = orderService.getOrderById((long) orderId);
        if (order == null) {
            System.out.println("订单不存在。");
            return;
        }

        List<OrderItem> items = orderService.getOrderItems((long) orderId);
        System.out.println("\n--- 订单详情 ---");
        System.out.println("订单号: " + order.getOrderNo());
        System.out.println("总价: " + order.getTotalAmount() + " 元");
        System.out.println("状态: " + order.getStatus());
        System.out.println("下单时间: " + order.getCreatedAt());
        System.out.println("\n菜品明细:");
        System.out.printf("%-15s %-10s %-10s %-10s%n", "菜品", "单价", "数量", "小计");
        System.out.println("-------------------------------------");
        for (OrderItem item : items) {
            System.out.printf("%-15s %-10.2f %-10d %-10.2f%n",
                    item.getMenuItemName(), item.getUnitPrice(), item.getQuantity(), item.getSubtotal());
        }
    }
}