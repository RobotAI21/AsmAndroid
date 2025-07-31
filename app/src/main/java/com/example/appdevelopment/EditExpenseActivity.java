package com.example.appdevelopment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.database.ExpenseRepository;

public class EditExpenseActivity extends AppCompatActivity {
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    RadioGroup rgExpenseCategory;
    Button btnSave, btnBack;
    ExpenseRepository repository;
    int expenseId, category;

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
        repository = new ExpenseRepository(EditExpenseActivity.this);

        expenseId = getIntent().getIntExtra("expense_id", -1);
        edtExpenseName.setText(getIntent().getStringExtra("expense_name"));
        edtExpenseMoney.setText(String.valueOf(getIntent().getIntExtra("expense_money", 0)));
        edtExpenseDescription.setText(getIntent().getStringExtra("expense_description"));
        category = getIntent().getIntExtra("expense_category", 0);
        rgExpenseCategory.check(rgExpenseCategory.getChildAt(category).getId());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtExpenseName.getText().toString().trim();
                String moneyStr = edtExpenseMoney.getText().toString().trim();
                String description = edtExpenseDescription.getText().toString().trim();
                int checkedId = rgExpenseCategory.getCheckedRadioButtonId();
                int category = rgExpenseCategory.indexOfChild(findViewById(checkedId));
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(moneyStr)) {
                    Toast.makeText(EditExpenseActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                int money = Integer.parseInt(moneyStr);
                int rows = repository.updateExpense(expenseId, name, money, description, category);
                if (rows > 0) {
                    Toast.makeText(EditExpenseActivity.this, "Expense updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditExpenseActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            };
        });
        // Set up the back button to return to the previous activity
       btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish(); // Close the current activity and return to the previous one
           }
       });
    }
}
