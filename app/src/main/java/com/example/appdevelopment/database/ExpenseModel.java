// Trong file: database/ExpenseModel.java

package com.example.appdevelopment.database;

public class ExpenseModel {
    private int id;
    private String name;
    private int money;
    private String description;
    private int category;

    // SỬA 1: Thêm thuộc tính status (kiểu số) vào đây
    private int status;

    private String createdAt;

    // SỬA 2: Sửa lại Constructor để nhận đủ 7 tham số
    // Bây giờ nó sẽ nhận cả status và createdAt
    public ExpenseModel(int id, String name, int money, String description, int category, int status, String createdAt) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.description = description;
        this.category = category;
        this.status = status; // Gán giá trị cho status
        this.createdAt = createdAt;
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

    // SỬA 3: Thêm getter và setter cho thuộc tính status mới
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}