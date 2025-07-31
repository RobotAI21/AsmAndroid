package com.example.appdevelopment.database;

public class ExpenseModel {
    public int id;
    public String name;
    public int money;
    public String description;
    public int category;
    public String createdAt;

    public ExpenseModel(int id, String name, int money, String description, int category, String createdAt) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.description = description;
        this.category = category;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
