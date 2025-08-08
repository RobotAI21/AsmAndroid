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

// CreateBudgetActivity là một màn hình (Activity) cho phép người dùng tạo một ngân sách mới.
public class CreateBudgetActivity extends AppCompatActivity {
    // Khai báo các thành phần giao diện người dùng (UI components)
    EditText edtNameBudget, edtMoneyBudget, edtDescription; // Các ô nhập liệu
    Button btnSave, btnBack; // Các nút bấm

    // Khai báo đối tượng Repository để tương tác với cơ sở dữ liệu
    BudgetRepository repository;

    // Khai báo các biến để lưu thông tin của người dùng hiện tại,
    // thông tin này được truyền từ màn hình trước đó.
    private int currentUserId;
    private String currentUsername;

    // onCreate là phương thức được gọi khi Activity được tạo lần đầu tiên.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gắn layout XML (giao diện) vào Activity này.
        setContentView(R.layout.activity_create_budget);

        // Ánh xạ các biến Java với các thành phần trong file layout XML qua ID của chúng.
        edtNameBudget = findViewById(R.id.edtBudgetName);
        edtMoneyBudget = findViewById(R.id.edtBudgetMoney);
        edtDescription = findViewById(R.id.edtBudgetDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBackBudget);

        // Khởi tạo đối tượng BudgetRepository để có thể thao tác với database.
        repository = new BudgetRepository(CreateBudgetActivity.this);

        // Lấy dữ liệu được gửi kèm từ Activity trước đó (thông qua Intent).
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras(); // Bundle chứa dữ liệu được đóng gói.

        // Kiểm tra xem bundle có tồn tại và chứa dữ liệu không.
        if (bundle != null) {
            // Lấy USER_ID và USERNAME. Nếu không tìm thấy, gán giá trị mặc định (-1 và "").
            currentUserId = bundle.getInt("USER_ID", -1);
            currentUsername = bundle.getString("USERNAME", "");
        } else {
            // Nếu không có thông tin người dùng được truyền sang, đây là một lỗi.
            // Hiển thị thông báo và đóng Activity để tránh các lỗi không mong muốn.
            Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình hiện tại.
            return;   // Dừng thực thi phương thức onCreate.
        }

        // Thiết lập sự kiện lắng nghe (listener) cho nút "Quay lại".
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SỬA 1: Tối ưu hóa. Chỉ cần gọi finish() để đóng màn hình hiện tại
                // và tự động quay về màn hình trước đó trên "ngăn xếp" (activity stack).
                // Không cần tạo lại MainMenuActivity một cách thủ công.
                finish();
            }
        });

        // Thiết lập sự kiện lắng nghe cho nút "Lưu".
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu người dùng đã nhập từ các ô EditText.
                // .toString() để chuyển thành chuỗi, .trim() để xóa khoảng trắng thừa.
                String name = edtNameBudget.getText().toString().trim();
                String moneyStr = edtMoneyBudget.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();

                // === BẮT ĐẦU KIỂM TRA DỮ LIỆU ĐẦU VÀO (VALIDATION) ===

                // Kiểm tra xem người dùng đã nhập tên ngân sách chưa.
                if (TextUtils.isEmpty(name)) {
                    // Nếu chưa, đặt thông báo lỗi trực tiếp trên ô nhập liệu và dừng lại.
                    edtNameBudget.setError("Enter Name Budget, PLS!!!!!!!!!");
                    return; // Dừng hàm onClick tại đây.
                }
                // Kiểm tra xem người dùng đã nhập số tiền chưa.
                if (TextUtils.isEmpty(moneyStr)) {
                    edtMoneyBudget.setError("Enter Money, PLS!!!!!!!!!");
                    return;
                }

                // Chuyển đổi chuỗi tiền thành số nguyên.
                int money = Integer.parseInt(moneyStr);
                // Kiểm tra số tiền phải là một số dương.
                if (money <= 0) {
                    edtMoneyBudget.setError("Money Can't Be Zero or Negative, PLS!!!!!!!!!");
                    return;
                }
                // === KẾT THÚC KIỂM TRA DỮ LIỆU ===


                // Gọi phương thức saveBudget từ repository để lưu dữ liệu vào database.
                // Truyền cả currentUserId để biết ngân sách này thuộc về người dùng nào.
                // SỬA 2 (QUAN TRỌNG NHẤT): Gọi đúng tên hàm là "saveBudget" với userId.
                long insertResult = repository.saveBudget(name, money, description, currentUserId);

                // Việc chèn vào database sẽ trả về ID của hàng mới được chèn, hoặc -1 nếu lỗi.
                // Kiểm tra xem việc lưu có thành công không.
                if (insertResult > 0) { // Thành công khi kết quả trả về > 0 (ID của hàng mới).
                    // Hiển thị thông báo thành công.
                    Toast.makeText(CreateBudgetActivity.this, "Create budget successfully", Toast.LENGTH_SHORT).show();
                    // SỬA 3: Tối ưu hóa. Gọi finish() để đóng màn hình và quay về màn hình trước.
                    finish();
                } else {
                    // Nếu insertResult <= 0, có nghĩa là đã có lỗi xảy ra.
                    Toast.makeText(CreateBudgetActivity.this, "Can not create budget", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}