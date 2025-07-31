package com.example.appdevelopment.budgets;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.MainMenuActivity;
import com.example.appdevelopment.database.ExpenseRepository;
import com.example.appdevelopment.R;

import java.util.Calendar;

public class CreateExpenseActivity extends AppCompatActivity {
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    Button btnSave, btnBack;
    ExpenseRepository repository;
    RadioGroup rgExpenseCategory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtExpenseMoney = findViewById(R.id.edtExpenseMoney);
        edtExpenseDescription = findViewById(R.id.edtBudgetDescription);

        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);
        repository = new ExpenseRepository(this);
        rgExpenseCategory = findViewById(R.id.rgExpenseCategory);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateExpenseActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtExpenseName.getText().toString().trim();
                String moneyStr = edtExpenseMoney.getText().toString().trim();
                String description = edtExpenseDescription.getText().toString().trim();

                int category = -1;
                int checkedId = rgExpenseCategory.getCheckedRadioButtonId();
                if (checkedId == R.id.rbRent) category = 0;
                else if (checkedId == R.id.rbGroceries) category = 1;
                else if (checkedId == R.id.rbTransportation) category = 2;
                else if (checkedId == R.id.rbOther) category = 3;

                if (TextUtils.isEmpty(name)) {
                    edtExpenseName.setError("Enter expense name");
                    return;
                }
                if (TextUtils.isEmpty(moneyStr)) {
                    edtExpenseMoney.setError("Enter amount");
                    return;
                }
                int money = Integer.parseInt(moneyStr);
                if (money <= 0) {
                    edtExpenseMoney.setError("Amount must be positive");
                    return;
                }
                if (category == -1) {
                    Toast.makeText(CreateExpenseActivity.this, "Select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                long insert = repository.addNewExpense(name, money, description, category);
                if (insert == -1) {
                    Toast.makeText(CreateExpenseActivity.this, "Cannot create expense", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateExpenseActivity.this, "Expense created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
