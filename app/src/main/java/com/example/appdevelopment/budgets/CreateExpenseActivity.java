// Trong file: budgets/CreateExpenseActivity.java

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
import com.example.appdevelopment.R;
import com.example.appdevelopment.database.ExpenseRepository;

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
        rgExpenseCategory = findViewById(R.id.rgExpenseCategory);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);

        repository = new ExpenseRepository(this);

        // Giữ nguyên logic của nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kết thúc Activity hiện tại để quay về màn hình trước đó
                finish();
            }
        });

        // Sửa lại logic của nút Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtExpenseName.getText().toString().trim();
                String moneyStr = edtExpenseMoney.getText().toString().trim();
                String description = edtExpenseDescription.getText().toString().trim();
                int checkedId = rgExpenseCategory.getCheckedRadioButtonId();

                // Kiểm tra các trường bắt buộc
                if (TextUtils.isEmpty(name)) {
                    edtExpenseName.setError("Enter expense name");
                    return;
                }
                if (TextUtils.isEmpty(moneyStr)) {
                    edtExpenseMoney.setError("Enter amount");
                    return;
                }
                if (checkedId == -1) {
                    Toast.makeText(CreateExpenseActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                int money = Integer.parseInt(moneyStr);

                // SỬA 1: Tối ưu cách lấy giá trị category.
                // Cách này sẽ tự động lấy vị trí của RadioButton được chọn (0, 1, 2, 3...)
                // Nó đáng tin cậy hơn và ngắn gọn hơn.
                int category = rgExpenseCategory.indexOfChild(findViewById(checkedId));

                // SỬA 2 (QUAN TRỌNG NHẤT): Gọi đúng tên hàm là "saveExpense".
                long insertResult = repository.saveExpense(name, money, description, category);

                if (insertResult > 0) { // Thành công khi kết quả trả về > 0
                    Toast.makeText(CreateExpenseActivity.this, "Expense created successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng Activity và quay về màn hình danh sách
                } else {
                    Toast.makeText(CreateExpenseActivity.this, "Failed to create expense", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}