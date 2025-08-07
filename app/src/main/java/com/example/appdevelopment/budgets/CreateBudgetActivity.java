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
    // Thêm biến để lưu thông tin user
    private int currentUserId;
    private String currentUsername;

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
        
        // Lấy thông tin user từ Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            currentUserId = bundle.getInt("USER_ID", -1);
            currentUsername = bundle.getString("USERNAME", "");
        } else {
            // Không có thông tin user, quay về MainMenuActivity
            Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SỬA 1: Tối ưu hóa. Chỉ cần gọi finish() để đóng màn hình hiện tại
                // và quay về màn hình trước đó, không cần tạo lại MainMenuActivity.
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtNameBudget.getText().toString().trim();
                String moneyStr = edtMoneyBudget.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtNameBudget.setError("Enter Name Budget, PLS!!!!!!!!!");
                    return;
                }
                if (TextUtils.isEmpty(moneyStr)) {
                    edtMoneyBudget.setError("Enter Money, PLS!!!!!!!!!");
                    return;
                }

                int money = Integer.parseInt(moneyStr);
                if (money <= 0) {
                    edtMoneyBudget.setError("Money Can't Be Zero or Negative, PLS!!!!!!!!!");
                    return;
                }

                // SỬA 2 (QUAN TRỌNG NHẤT): Gọi đúng tên hàm là "saveBudget" với userId.
                long insertResult = repository.saveBudget(name, money, description, currentUserId);

                if (insertResult > 0) { // Thành công khi kết quả trả về > 0
                    Toast.makeText(CreateBudgetActivity.this, "Create budget successfully", Toast.LENGTH_SHORT).show();
                    // SỬA 3: Tối ưu hóa. Gọi finish() để quay về.
                    finish();
                } else {
                    Toast.makeText(CreateBudgetActivity.this, "Can not create budget", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}