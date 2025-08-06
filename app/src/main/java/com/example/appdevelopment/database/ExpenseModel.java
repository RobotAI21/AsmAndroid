package com.example.appdevelopment.database;

public class ExpenseModel {
    private int id;
    private String name;
    private int money;
    private String description;
    private int category;
    private int status;
    private String createdAt;
    // Thêm trường để lưu budget_id
    private int budgetId;

    // Sửa Constructor để nhận đủ tham số
    public ExpenseModel(int id, String name, int money, String description, int category, int status, String createdAt, int budgetId) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.budgetId = budgetId;
    }

    // Thêm getter và setter cho budgetId
    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    // --- Giữ nguyên các hàm getter/setter cũ ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}