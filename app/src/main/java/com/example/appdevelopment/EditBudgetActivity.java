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

/**
 * Activity để chỉnh sửa thông tin ngân sách
 * Cho phép người dùng cập nhật tên, số tiền và mô tả của ngân sách
 */
public class EditBudgetActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    EditText edtNameBudget, edtMoneyBudget, edtDescription;
    Button btnSave, btnBack;
    BudgetRepository repository;
    int budgetId;

    /**
     * Phương thức khởi tạo Activity
     * Thiết lập giao diện và xử lý các sự kiện
     * @param savedInstanceState Bundle chứa trạng thái trước đó của Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_budget);

        // Khởi tạo các thành phần UI
        edtNameBudget = findViewById(R.id.edtBudgetName);
        edtMoneyBudget = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtBudgetDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackBudget);
        
        // Khởi tạo repository để thao tác với cơ sở dữ liệu
        repository = new BudgetRepository(EditBudgetActivity.this);
        
        // Lấy thông tin ngân sách từ Intent để hiển thị lên form
        budgetId = getIntent().getIntExtra("BUDGET_ID", -1);
        edtNameBudget.setText(getIntent().getStringExtra("BUDGET_NAME"));
        edtMoneyBudget.setText(String.valueOf(getIntent().getIntExtra("BUDGET_MONEY",0)));
        edtDescription.setText(getIntent().getStringExtra("BUDGET_DESCRIPTION"));
        
        // Xử lý sự kiện khi nhấn nút Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các trường nhập liệu
                String name = edtNameBudget.getText().toString().trim();
                String money = edtMoneyBudget.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                
                // Kiểm tra tính hợp lệ của dữ liệu
                if(TextUtils.isEmpty(name)|| TextUtils.isEmpty(money)){
                    Toast.makeText(EditBudgetActivity.this, "Pls fill all field!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Chuyển đổi và cập nhật dữ liệu
                int moneyInt = Integer.parseInt(money);
                int rows = repository.updateBudget(budgetId, name, moneyInt, description);
                
                // Hiển thị thông báo kết quả
                if(rows >0){
                    Toast.makeText(EditBudgetActivity.this, "Update budget successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng Activity
                } else {
                    Toast.makeText(EditBudgetActivity.this, "Update budget failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        // Xử lý sự kiện khi nhấn nút Quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity hiện tại và quay về Activity trước đó
            }
        });
    }
}
