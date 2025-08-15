package com.example.appdevelopment.database;

/**
 * Model class đại diện cho một ngân sách trong ứng dụng
 * Chứa thông tin về ngân sách và số tiền còn lại
 */
public class BudgetModel {
    // Các thuộc tính cơ bản của ngân sách
    private int id;
    private String nameBudget;
    private int moneyBudget;
    private String description;
    private int statusBudget;
    private String createdAt;
    private String updatedAt;
    private int userId;

    // Thuộc tính tính toán: số tiền còn lại (không lưu vào DB)
    private int remainingMoney;

    /**
     * Constructor của BudgetModel
     * @param id ID của ngân sách
     * @param nameBudget Tên ngân sách
     * @param moneyBudget Số tiền ngân sách
     * @param description Mô tả ngân sách
     * @param statusBudget Trạng thái ngân sách
     * @param createdAt Thời gian tạo
     * @param updatedAt Thời gian cập nhật
     * @param userId ID của người dùng sở hữu ngân sách
     */
    public BudgetModel(int id, String nameBudget, int moneyBudget, String description, int statusBudget, String createdAt, String updatedAt, int userId) {
        this.id = id;
        this.nameBudget = nameBudget;
        this.moneyBudget = moneyBudget;
        this.description = description;
        this.statusBudget = statusBudget;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        // Khởi tạo số tiền còn lại bằng số tiền ngân sách ban đầu
        this.remainingMoney = moneyBudget;
    }

    /**
     * Lấy số tiền còn lại của ngân sách
     * @return Số tiền còn lại
     */
    public int getRemainingMoney() {
        return remainingMoney;
    }

    /**
     * Cập nhật số tiền còn lại của ngân sách
     * @param remainingMoney Số tiền còn lại mới
     */
    public void setRemainingMoney(int remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    /**
     * Phương thức toString để hiển thị tên ngân sách
     * @return Tên ngân sách
     */
    @Override
    public String toString() {
        return this.nameBudget;
    }

    // --- Các getter và setter cho các thuộc tính cơ bản ---
    
    /**
     * Lấy ID của ngân sách
     * @return ID ngân sách
     */
    public int getId() { return id; }
    
    /**
     * Cập nhật ID của ngân sách
     * @param id ID mới
     */
    public void setId(int id) { this.id = id; }
    
    /**
     * Lấy tên ngân sách
     * @return Tên ngân sách
     */
    public String getNameBudget() { return nameBudget; }
    
    /**
     * Cập nhật tên ngân sách
     * @param nameBudget Tên mới
     */
    public void setNameBudget(String nameBudget) { this.nameBudget = nameBudget; }
    
    /**
     * Lấy số tiền ngân sách
     * @return Số tiền ngân sách
     */
    public int getMoneyBudget() { return moneyBudget; }
    
    /**
     * Cập nhật số tiền ngân sách
     * @param moneyBudget Số tiền mới
     */
    public void setMoneyBudget(int moneyBudget) { this.moneyBudget = moneyBudget; }
    
    /**
     * Lấy mô tả ngân sách
     * @return Mô tả ngân sách
     */
    public String getDescription() { return description; }
    
    /**
     * Cập nhật mô tả ngân sách
     * @param description Mô tả mới
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Lấy trạng thái ngân sách
     * @return Trạng thái ngân sách
     */
    public int getStatusBudget() { return statusBudget; }
    
    /**
     * Cập nhật trạng thái ngân sách
     * @param statusBudget Trạng thái mới
     */
    public void setStatusBudget(int statusBudget) { this.statusBudget = statusBudget; }
    
    /**
     * Lấy thời gian tạo
     * @return Thời gian tạo
     */
    public String getCreatedAt() { return createdAt; }
    
    /**
     * Cập nhật thời gian tạo
     * @param createdAt Thời gian tạo mới
     */
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    /**
     * Lấy thời gian cập nhật
     * @return Thời gian cập nhật
     */
    public String getUpdatedAt() { return updatedAt; }
    
    /**
     * Cập nhật thời gian cập nhật
     * @param updatedAt Thời gian cập nhật mới
     */
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Lấy ID người dùng sở hữu ngân sách
     * @return ID người dùng
     */
    public int getUserId() { return userId; }
    
    /**
     * Cập nhật ID người dùng sở hữu ngân sách
     * @param userId ID người dùng mới
     */
    public void setUserId(int userId) { this.userId = userId; }
}