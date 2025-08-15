package com.example.appdevelopment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.example.appdevelopment.database.ExpenseRepository;
import com.example.appdevelopment.utils.Notification;
import java.util.List;

/**
 * Activity chỉnh sửa thông tin chi tiêu
 * Cho phép người dùng cập nhật tên, số tiền, mô tả và ngân sách của chi tiêu
 */
public class EditExpenseActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    Spinner spinnerBudgetSelection;
    Button btnSave, btnBack;

    // Khai báo các repository để thao tác với cơ sở dữ liệu
    ExpenseRepository repository;
    BudgetRepository budgetRepository;
    
    // Danh sách ngân sách và thông tin chi tiêu
    List<BudgetModel> budgetList;
    int expenseId, initialBudgetId;

    /**
     * Phương thức khởi tạo Activity
     * Thiết lập giao diện và xử lý các sự kiện
     * @param savedInstanceState Bundle chứa trạng thái trước đó của Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        // Khởi tạo các thành phần UI
        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtExpenseMoney = findViewById(R.id.edtExpenseMoney);
        edtExpenseDescription = findViewById(R.id.edtBudgetDescription);
        spinnerBudgetSelection = findViewById(R.id.spinner_budget_selection);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);

        // Khởi tạo các repository
        repository = new ExpenseRepository(this);
        budgetRepository = new BudgetRepository(this);

        // Lấy thông tin chi tiêu từ Intent
        expenseId = getIntent().getIntExtra("expense_id", -1);
        initialBudgetId = getIntent().getIntExtra("expense_budget_id", -1);

        // Hiển thị thông tin chi tiêu hiện tại lên form
        edtExpenseName.setText(getIntent().getStringExtra("expense_name"));
        edtExpenseMoney.setText(String.valueOf(getIntent().getIntExtra("expense_money", 0)));
        edtExpenseDescription.setText(getIntent().getStringExtra("expense_description"));

        // Thiết lập spinner và xử lý sự kiện
        setupBudgetSpinner();
        btnSave.setOnClickListener(v -> updateExpense());
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Phương thức thiết lập spinner chọn ngân sách
     * Tạo adapter và chọn ngân sách hiện tại của chi tiêu
     */
    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getAllBudgets();
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);

        // Chọn ngân sách hiện tại của chi tiêu
        if (initialBudgetId != -1) {
            for (int i = 0; i < budgetList.size(); i++) {
                if (budgetList.get(i).getId() == initialBudgetId) {
                    spinnerBudgetSelection.setSelection(i);
                    break;
                }
            }
        }
    }

    /**
     * Phương thức cập nhật chi tiêu
     * Kiểm tra dữ liệu và cập nhật vào cơ sở dữ liệu
     */
    private void updateExpense() {
        // Lấy dữ liệu từ form
        String name = edtExpenseName.getText().toString().trim();
        String moneyStr = edtExpenseMoney.getText().toString().trim();
        String description = edtExpenseDescription.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(moneyStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerBudgetSelection.getSelectedItem() == null) {
            Toast.makeText(this, "No budget selected. Please create one first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi và lấy thông tin ngân sách
        int money = Integer.parseInt(moneyStr);
        BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
        int budgetId = selectedBudget.getId();

        // Cập nhật chi tiêu vào cơ sở dữ liệu
        int rows = repository.updateExpense(expenseId, name, money, description, 1, budgetId);

        if (rows > 0) {
            // Reset thông báo cho ngân sách cũ
            String initialBudgetName = "";
            for (BudgetModel budget : budgetList) {
                if (budget.getId() == initialBudgetId) {
                    initialBudgetName = budget.getNameBudget();
                    break;
                }
            }
            if (!initialBudgetName.isEmpty()) {
                Notification.resetBudgetNotifications(this, initialBudgetName);
            }
            
            // Reset thông báo cho ngân sách mới nếu khác ngân sách cũ
            if (initialBudgetId != budgetId) {
                Notification.resetBudgetNotifications(this, selectedBudget.getNameBudget());
            }

            // Kiểm tra và thông báo nếu vượt quá ngân sách
            checkBudgetLimitAndNotify(selectedBudget, money);
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Phương thức kiểm tra giới hạn ngân sách và gửi thông báo
     * @param budget Ngân sách cần kiểm tra
     * @param newExpenseAmount Số tiền chi tiêu mới
     */
    private void checkBudgetLimitAndNotify(BudgetModel budget, int newExpenseAmount) {
        // Lấy tổng chi tiêu cho ngân sách này
        int totalExpenses = repository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), 1); // Default userId = 1
        int budgetLimit = budget.getMoneyBudget();
        
        // Tính toán ngân sách còn lại
        int remainingBudget = budgetLimit - totalExpenses;
        
        if (remainingBudget <= 0) {
            // Thông báo vượt quá ngân sách
            Notification.showBudgetExceeded(
                this,
                budget.getNameBudget(),
                totalExpenses,
                budgetLimit
            );
        } else if (remainingBudget <= budgetLimit * 0.1) {
            // Cảnh báo ngân sách (còn ít hơn 10%)
            Notification.showBudgetWarning(
                this,
                budget.getNameBudget(),
                remainingBudget
            );
        }
    }
}