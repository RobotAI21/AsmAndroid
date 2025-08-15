package com.example.appdevelopment;

import android.content.Context;
import android.widget.Toast;

import com.example.appdevelopment.database.BudgetRepository;

/**
 * Test class để kiểm tra chức năng xóa budget
 * Demo cách sử dụng các phương thức kiểm tra và xóa budget
 */
public class TestBudgetDelete {
    
    private Context context;
    private BudgetRepository budgetRepository;
    
    /**
     * Constructor của TestBudgetDelete
     * @param context Context của ứng dụng
     */
    public TestBudgetDelete(Context context) {
        this.context = context;
        this.budgetRepository = new BudgetRepository(context);
    }
    
    /**
     * Phương thức test kiểm tra budget có expense hay không
     * @param budgetId ID của budget cần kiểm tra
     */
    public void testCheckBudgetExpenses(int budgetId) {
        boolean hasExpenses = budgetRepository.hasExpenses(budgetId);
        int expenseCount = budgetRepository.getExpenseCount(budgetId);
        
        String message = "Budget ID " + budgetId + ":\n";
        message += "Có expense: " + (hasExpenses ? "Có" : "Không") + "\n";
        message += "Số lượng expense: " + expenseCount;
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Phương thức test xóa budget
     * @param budgetId ID của budget cần xóa
     */
    public void testDeleteBudget(int budgetId) {
        // Kiểm tra trước khi xóa
        boolean hasExpenses = budgetRepository.hasExpenses(budgetId);
        int expenseCount = budgetRepository.getExpenseCount(budgetId);
        
        if (hasExpenses) {
            String message = "Budget có " + expenseCount + " expense.\n";
            message += "Việc xóa sẽ xóa tất cả expense liên quan.";
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
        
        // Thực hiện xóa
        int result = budgetRepository.deleteBudget(budgetId);
        
        String resultMessage = "Kết quả xóa budget ID " + budgetId + ":\n";
        resultMessage += result > 0 ? "Thành công" : "Thất bại";
        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Phương thức demo đầy đủ quy trình xóa budget
     * @param budgetId ID của budget cần xóa
     */
    public void demoBudgetDeletion(int budgetId) {
        // Bước 1: Kiểm tra budget có tồn tại không
        if (budgetRepository.getBudgetById(budgetId) == null) {
            Toast.makeText(context, "Budget không tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Bước 2: Kiểm tra có expense không
        boolean hasExpenses = budgetRepository.hasExpenses(budgetId);
        int expenseCount = budgetRepository.getExpenseCount(budgetId);
        
        if (hasExpenses) {
            // Bước 3: Hiển thị cảnh báo nếu có expense
            String warningMessage = "CẢNH BÁO: Budget có " + expenseCount + " expense!\n";
            warningMessage += "Việc xóa budget sẽ xóa tất cả expense liên quan.\n";
            warningMessage += "Bạn có chắc chắn muốn tiếp tục?";
            
            Toast.makeText(context, warningMessage, Toast.LENGTH_LONG).show();
            
            // Trong thực tế, ở đây sẽ hiển thị AlertDialog
            // Nhưng trong test này chỉ hiển thị Toast
        }
        
        // Bước 4: Thực hiện xóa
        int result = budgetRepository.deleteBudget(budgetId);
        
        // Bước 5: Hiển thị kết quả
        String resultMessage = result > 0 ? 
            "Xóa budget thành công!" : 
            "Xóa budget thất bại!";
        Toast.makeText(context, resultMessage, Toast.LENGTH_SHORT).show();
    }
}
