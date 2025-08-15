package com.example.appdevelopment.budgets;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdevelopment.R;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.example.appdevelopment.database.ExpenseRepository;
import com.example.appdevelopment.utils.Notification;
import java.util.List;

/**
 * Activity tạo chi tiêu mới
 * Cho phép người dùng tạo một chi tiêu mới với tên, số tiền, mô tả và chọn ngân sách
 */
public class CreateExpenseActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    Button btnSave, btnBack;
    Spinner spinnerBudgetSelection;
    
    // Khai báo các repository để thao tác với cơ sở dữ liệu
    ExpenseRepository repository;
    BudgetRepository budgetRepository;
    
    // Danh sách ngân sách và thông tin người dùng
    List<BudgetModel> budgetList;
    private int currentUserId;
    private String currentUsername;

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
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);
        spinnerBudgetSelection = findViewById(R.id.spinner_budget_selection);

        // Khởi tạo các repository
        repository = new ExpenseRepository(this);
        budgetRepository = new BudgetRepository(this);
        
        // Lấy thông tin người dùng từ Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            currentUserId = bundle.getInt("USER_ID", -1);
            currentUsername = bundle.getString("USERNAME", "");
        } else {
            Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thiết lập spinner và xử lý sự kiện
        setupBudgetSpinner();
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    /**
     * Phương thức thiết lập spinner chọn ngân sách
     * Tạo adapter và load danh sách ngân sách của người dùng
     */
    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getBudgetsByUserId(currentUserId);
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);
    }

    /**
     * Phương thức lưu chi tiêu mới
     * Kiểm tra dữ liệu và lưu vào cơ sở dữ liệu
     */
    private void saveExpense() {
        // Lấy dữ liệu từ form
        String name = edtExpenseName.getText().toString().trim();
        String moneyStr = edtExpenseMoney.getText().toString().trim();
        String description = edtExpenseDescription.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu
        if (TextUtils.isEmpty(name)) {
            edtExpenseName.setError("Enter expense name");
            return;
        }
        if (TextUtils.isEmpty(moneyStr)) {
            edtExpenseMoney.setError("Enter amount");
            return;
        }
        if (spinnerBudgetSelection.getSelectedItem() == null) {
            Toast.makeText(this, "Please create a budget first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi và lấy thông tin ngân sách
        int money = Integer.parseInt(moneyStr);
        BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
        int budgetId = selectedBudget.getId();

        // Lưu chi tiêu vào cơ sở dữ liệu
        long insertResult = repository.saveExpense(name, money, description, 1, budgetId, currentUserId);

        if (insertResult > 0) {
            // Kiểm tra và thông báo nếu vượt quá ngân sách
            checkBudgetLimitAndNotify(selectedBudget, money);
            Toast.makeText(this, "Expense created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create expense", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Phương thức kiểm tra giới hạn ngân sách và gửi thông báo
     * @param budget Ngân sách cần kiểm tra
     * @param newExpenseAmount Số tiền chi tiêu mới
     */
    private void checkBudgetLimitAndNotify(BudgetModel budget, int newExpenseAmount) {
        // Lấy tổng chi tiêu cho ngân sách này
        int totalExpenses = repository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), currentUserId);
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