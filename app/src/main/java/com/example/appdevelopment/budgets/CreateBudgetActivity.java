package com.example.appdevelopment.budgets;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.MainMenuActivity;
import com.example.appdevelopment.R;
import com.example.appdevelopment.database.BudgetRepository;

public class CreateBudgetActivity extends AppCompatActivity {
    EditText edtNameBudget, edtMoneyBudget, edtDescription;
    Button btnSave, btnBack;
    BudgetRepository repository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);
        edtNameBudget = findViewById(R.id.edtBudgetName);
        edtMoneyBudget = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtBudgetDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackBudget);
        repository = new BudgetRepository(CreateBudgetActivity.this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateBudgetActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtNameBudget.getText().toString().trim();
                int money = Integer.parseInt(edtMoneyBudget.getText().toString().trim());
                String description = edtDescription.getText().toString().trim();
                if (TextUtils.isEmpty(name)){
                    edtNameBudget.setError("Enter Name Budget, PLS!!!!!!!!!");
                    return;
                }
                if (money <=0){
                    edtMoneyBudget.setError("Money Can't Be Zero or Negative, PLS!!!!!!!!!");
                    return;
                }
                long insert = repository.addNewBudget(name, money, description);
                if (insert ==-1){
                    Toast.makeText(CreateBudgetActivity.this, "Can not create budget", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(CreateBudgetActivity.this, "Create budget successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateBudgetActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
