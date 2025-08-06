package com.example.appdevelopment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
import com.example.appdevelopment.database.ExpenseRepository;
import java.util.List;

public class EditExpenseActivity extends AppCompatActivity {
    EditText edtExpenseName, edtExpenseMoney, edtExpenseDescription;
    RadioGroup rgExpenseCategory;
    Spinner spinnerBudgetSelection; // Thêm Spinner
    Button btnSave, btnBack;

    ExpenseRepository repository;
    BudgetRepository budgetRepository;
    List<BudgetModel> budgetList;

    int expenseId, initialCategoryId, initialBudgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Giả sử bạn dùng chung layout với màn hình Create
        setContentView(R.layout.activity_create_expense);

        // Ánh xạ View
        edtExpenseName = findViewById(R.id.edtExpenseName);
        edtExpenseMoney = findViewById(R.id.edtExpenseMoney);
        edtExpenseDescription = findViewById(R.id.edtBudgetDescription);
        rgExpenseCategory = findViewById(R.id.rgExpenseCategory);
        spinnerBudgetSelection = findViewById(R.id.spinner_budget_selection); // Thay ID cho đúng
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackExpense);

        // Khởi tạo repository
        repository = new ExpenseRepository(this);
        budgetRepository = new BudgetRepository(this);

        // Nhận dữ liệu từ Intent
        expenseId = getIntent().getIntExtra("expense_id", -1);
        initialCategoryId = getIntent().getIntExtra("expense_category", 0);
        initialBudgetId = getIntent().getIntExtra("expense_budget_id", -1);

        // Điền dữ liệu cũ vào các trường
        edtExpenseName.setText(getIntent().getStringExtra("expense_name"));
        edtExpenseMoney.setText(String.valueOf(getIntent().getIntExtra("expense_money", 0)));
        edtExpenseDescription.setText(getIntent().getStringExtra("expense_description"));
        rgExpenseCategory.check(rgExpenseCategory.getChildAt(initialCategoryId).getId());

        // Cài đặt Spinner
        setupBudgetSpinner();

        // Xử lý sự kiện click
        btnSave.setOnClickListener(v -> updateExpense());
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupBudgetSpinner() {
        budgetList = budgetRepository.getAllBudgets();
        ArrayAdapter<BudgetModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudgetSelection.setAdapter(adapter);

        // Tìm và chọn sẵn ngân sách cũ của khoản chi này
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
        int checkedId = rgExpenseCategory.getCheckedRadioButtonId();
        int category = rgExpenseCategory.indexOfChild(findViewById(checkedId));

        // Lấy ID của budget được chọn từ Spinner
        BudgetModel selectedBudget = (BudgetModel) spinnerBudgetSelection.getSelectedItem();
        int budgetId = selectedBudget.getId();

        // Gọi hàm updateExpense với đủ 6 tham số
        int rows = repository.updateExpense(expenseId, name, money, description, category, budgetId);

        if (rows > 0) {
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}