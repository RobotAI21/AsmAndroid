package com.example.appdevelopment.budgets;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdevelopment.R;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.example.appdevelopment.database.ExpenseRepository;
import java.util.List;

public class CreateExpenseActivity extends AppCompatActivity {
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    Button btnSave, btnBack;
    ExpenseRepository repository;
    RadioGroup rgExpenseCategory;
    Spinner spinnerBudgetSelection; // Thêm spinner
    BudgetRepository budgetRepository;
    List<BudgetModel> budgetList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtExpenseMoney = findViewById(R.id.edtExpenseMoney);
        edtExpenseDescription = findViewById(R.id.edtBudgetDescription);
        rgExpenseCategory = findViewById(R.id.rgExpenseCategory);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);
        spinnerBudgetSelection = findViewById(R.id.spinner_budget_selection); // Thay ID cho đúng

        repository = new ExpenseRepository(this);
        budgetRepository = new BudgetRepository(this);

        setupBudgetSpinner();

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveExpense());

    }

    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getAllBudgets();
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);
    }

    private void saveExpense() {
        String name = edtExpenseName.getText().toString().trim();
        String moneyStr = edtExpenseMoney.getText().toString().trim();
        String description = edtExpenseDescription.getText().toString().trim();
        int checkedId = rgExpenseCategory.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(name)) {
            edtExpenseName.setError("Enter expense name");
            return;
        }
        if (TextUtils.isEmpty(moneyStr)) {
            edtExpenseMoney.setError("Enter amount");
            return;
        }
        if (checkedId == -1) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerBudgetSelection.getSelectedItem() == null) {
            Toast.makeText(this, "Please create a budget first", Toast.LENGTH_SHORT).show();
            return;
        }

        int money = Integer.parseInt(moneyStr);
        int category = rgExpenseCategory.indexOfChild(findViewById(checkedId));

        BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
        int budgetId = selectedBudget.getId();

        long insertResult = repository.saveExpense(name, money, description, category, budgetId);

        if (insertResult > 0) {
            Toast.makeText(this, "Expense created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create expense", Toast.LENGTH_SHORT).show();
        }
    }
}