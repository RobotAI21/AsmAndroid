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

public class EditExpenseActivity extends AppCompatActivity {
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    Spinner spinnerBudgetSelection;
    Button btnSave, btnBack;

    ExpenseRepository repository;
    BudgetRepository budgetRepository;
    List<BudgetModel> budgetList;

    int expenseId, initialBudgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtExpenseMoney = findViewById(R.id.edtExpenseMoney);
        edtExpenseDescription = findViewById(R.id.edtBudgetDescription);
        spinnerBudgetSelection = findViewById(R.id.spinner_budget_selection);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);

        repository = new ExpenseRepository(this);
        budgetRepository = new BudgetRepository(this);

        expenseId = getIntent().getIntExtra("expense_id", -1);
        initialBudgetId = getIntent().getIntExtra("expense_budget_id", -1);

        edtExpenseName.setText(getIntent().getStringExtra("expense_name"));
        edtExpenseMoney.setText(String.valueOf(getIntent().getIntExtra("expense_money", 0)));
        edtExpenseDescription.setText(getIntent().getStringExtra("expense_description"));

        setupBudgetSpinner();
        btnSave.setOnClickListener(v -> updateExpense());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getAllBudgets();
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);

        if (initialBudgetId != -1) {
            for (int i = 0; i < budgetList.size(); i++) {
                if (budgetList.get(i).getId() == initialBudgetId) {
                    spinnerBudgetSelection.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateExpense() {
        String name = edtExpenseName.getText().toString().trim();
        String moneyStr = edtExpenseMoney.getText().toString().trim();
        String description = edtExpenseDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(moneyStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerBudgetSelection.getSelectedItem() == null) {
            Toast.makeText(this, "No budget selected. Please create one first.", Toast.LENGTH_SHORT).show();
            return;
        }

        int money = Integer.parseInt(moneyStr);
        BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
        int budgetId = selectedBudget.getId();

        int rows = repository.updateExpense(expenseId, name, money, description, 1, budgetId);

        if (rows > 0) {
            // Kiểm tra và thông báo nếu vượt quá ngân sách
            checkBudgetLimitAndNotify(selectedBudget, money);
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBudgetLimitAndNotify(BudgetModel budget, int newExpenseAmount) {
        // Lấy tổng chi tiêu cho budget này
        int totalExpenses = repository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), 1); // Default userId = 1
        int budgetLimit = budget.getMoneyBudget();
        
        // Tính toán ngân sách còn lại
        int remainingBudget = budgetLimit - totalExpenses;
        
        if (remainingBudget <= 0) {
            // Vượt quá ngân sách
            Notification.showBudgetExceededNotification(
                this,
                budget.getNameBudget(),
                totalExpenses,
                budgetLimit
            );
        } else if (remainingBudget <= budgetLimit * 0.1) {
            // Cảnh báo ngân sách (còn 10%)
            Notification.showBudgetWarningNotification(
                this,
                budget.getNameBudget(),
                remainingBudget
            );
        }
    }
}