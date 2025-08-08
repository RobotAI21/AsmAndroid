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

public class CreateExpenseActivity extends AppCompatActivity {
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    Button btnSave, btnBack;
    ExpenseRepository repository;
    Spinner spinnerBudgetSelection;
    BudgetRepository budgetRepository;
    List<BudgetModel> budgetList;
    private int currentUserId;
    private String currentUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtExpenseMoney = findViewById(R.id.edtExpenseMoney);
        edtExpenseDescription = findViewById(R.id.edtBudgetDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);
        spinnerBudgetSelection = findViewById(R.id.spinner_budget_selection);

        repository = new ExpenseRepository(this);
        budgetRepository = new BudgetRepository(this);
        
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

        setupBudgetSpinner();
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getBudgetsByUserId(currentUserId);
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);
    }

    private void saveExpense() {
        String name = edtExpenseName.getText().toString().trim();
        String moneyStr = edtExpenseMoney.getText().toString().trim();
        String description = edtExpenseDescription.getText().toString().trim();

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

        int money = Integer.parseInt(moneyStr);
        BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
        int budgetId = selectedBudget.getId();

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

    private void checkBudgetLimitAndNotify(BudgetModel budget, int newExpenseAmount) {
        // Lấy tổng chi tiêu cho budget này
        int totalExpenses = repository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), currentUserId);
        int budgetLimit = budget.getMoneyBudget();
        
        // Tính toán ngân sách còn lại
        int remainingBudget = budgetLimit - totalExpenses;
        
        if (remainingBudget <= 0) {
            // Vượt quá ngân sách
            Notification.showBudgetExceeded(
                this,
                budget.getNameBudget(),
                totalExpenses,
                budgetLimit
            );

        } else if (remainingBudget <= budgetLimit * 0.1) {
            // Cảnh báo ngân sách (còn 10%)
            Notification.showBudgetWarning(
                this,
                budget.getNameBudget(),
                remainingBudget
            );
        }
    }
}