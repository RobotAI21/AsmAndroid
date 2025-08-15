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

/**
 * Activity tạo ngân sách mới
 * Cho phép người dùng tạo một ngân sách mới với tên, số tiền và mô tả
 */
public class CreateBudgetActivity extends AppCompatActivity {
    // Khai báo các thành phần giao diện người dùng
    EditText edtNameBudget, edtMoneyBudget, edtDescription;
    Button btnSave, btnBack;

    // Khai báo repository để thao tác với cơ sở dữ liệu
    BudgetRepository repository;

    // Biến lưu thông tin người dùng hiện tại
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
        // Gắn layout XML vào Activity
        setContentView(R.layout.activity_create_budget);

        // Khởi tạo các thành phần UI
        edtNameBudget = findViewById(R.id.edtBudgetName);
        edtMoneyBudget = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtBudgetDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackBudget);

        // Khởi tạo repository để thao tác với cơ sở dữ liệu
        repository = new BudgetRepository(CreateBudgetActivity.this);

        // Lấy thông tin người dùng từ Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Kiểm tra và lấy thông tin người dùng
        if (bundle != null) {
            currentUserId = bundle.getInt("USER_ID", -1);
            currentUsername = bundle.getString("USERNAME", "");
        } else {
            // Hiển thị thông báo lỗi nếu không có thông tin người dùng
            Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý sự kiện nút Quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Activity hiện tại và quay về màn hình trước đó
                finish();
            }
        });

        // Xử lý sự kiện nút Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ form
                String name = edtNameBudget.getText().toString().trim();
                String moneyStr = edtMoneyBudget.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();

                // Kiểm tra tính hợp lệ của dữ liệu đầu vào
                if (TextUtils.isEmpty(name)) {
                    edtNameBudget.setError("Enter Name Budget, PLS!!!!!!!!!");
                    return;
                }
                if (TextUtils.isEmpty(moneyStr)) {
                    edtMoneyBudget.setError("Enter Money, PLS!!!!!!!!!");
                    return;
                }

                // Chuyển đổi chuỗi tiền thành số nguyên
                int money = Integer.parseInt(moneyStr);
                if (money <= 0) {
                    edtMoneyBudget.setError("Money Can't Be Zero or Negative, PLS!!!!!!!!!");
                    return;
                }

                // Lưu ngân sách vào cơ sở dữ liệu
                long insertResult = repository.saveBudget(name, money, description, currentUserId);

                // Kiểm tra kết quả lưu
                if (insertResult > 0) {
                    // Hiển thị thông báo thành công
                    Toast.makeText(CreateBudgetActivity.this, "Create budget successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Hiển thị thông báo lỗi
                    Toast.makeText(CreateBudgetActivity.this, "Can not create budget", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}