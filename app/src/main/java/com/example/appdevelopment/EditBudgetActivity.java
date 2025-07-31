package com.example.appdevelopment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.database.BudgetRepository;

public class EditBudgetActivity extends AppCompatActivity {
    EditText edtNameBudget, edtMoneyBudget, edtDescription;
    Button btnSave, btnBack;
    BudgetRepository repository;
    int budgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);

        edtNameBudget = findViewById(R.id.edtBudgetName);
        edtMoneyBudget = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtBudgetDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackBudget);
        repository = new BudgetRepository(EditBudgetActivity.this);
        // Get the budget ID from the intent
        budgetId = getIntent().getIntExtra("BUDGET_ID", -1);
        edtNameBudget.setText(getIntent().getStringExtra("BUDGET_NAME"));
        edtMoneyBudget.setText(String.valueOf(getIntent().getIntExtra("BUDGET_MONEY",0)));
        edtDescription.setText(getIntent().getStringExtra("BUDGET_DESCRIPTION"));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtNameBudget.getText().toString().trim();
                String money = edtMoneyBudget.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                if(TextUtils.isEmpty(name)|| TextUtils.isEmpty(money)){
                    Toast.makeText(EditBudgetActivity.this, "Pls fill all field!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int moneyInt = Integer.parseInt(money);
                int rows = repository.updateBudget(budgetId, name, moneyInt, description);
                if(rows >0){
                    Toast.makeText(EditBudgetActivity.this, "Update budget successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(EditBudgetActivity.this, "Update budget failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to the previous one
            }
        });
    }
}
