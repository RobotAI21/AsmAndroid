package com.example.appdevelopment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity để test các sự kiện cơ bản trong Android
 * Demo các loại sự kiện: click, text change, checkbox, radio button
 */
public class TestEventActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    private EditText edtData;
    private Button btnClick, btnClear;
    private CheckBox cbBlock;
    private TextView tvTitle;
    private RadioGroup radAddress;
    private RadioButton radHN, radSL, radHG;
    
    /**
     * Phương thức khởi tạo Activity
     * Thiết lập giao diện và xử lý các sự kiện
     * @param savedInstanceState Bundle chứa trạng thái trước đó của Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_event);
        
        // Khởi tạo các thành phần UI
        edtData = findViewById(R.id.edtData);
        btnClick = findViewById(R.id.btnClick);
        btnClear = findViewById(R.id.btnClear);
        cbBlock = findViewById(R.id.cbBlock);
        tvTitle = findViewById(R.id.tvTitle);
        radAddress = findViewById(R.id.radAddress);
        radHN = findViewById(R.id.radHN);
        radSL = findViewById(R.id.radSL);
        radHG = findViewById(R.id.radHG);

        // Khóa các thành phần UI ban đầu
        edtData.setEnabled(false);
        btnClear.setEnabled(false);
        btnClick.setEnabled(false);

        // Xử lý sự kiện thay đổi text cho EditText
        edtData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Được gọi trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Được gọi khi text đang thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Được gọi sau khi text đã thay đổi
                String content = s.toString().trim(); // Lấy nội dung nhập vào text
                tvTitle.setText(content);
            }
        });

        // Xử lý sự kiện click cho nút Clear
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtData.setText("");
            }
        });

        // Xử lý sự kiện click cho nút Click
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu người dùng nhập vào
                String data = edtData.getText().toString().trim();
                if (TextUtils.isEmpty(data)) {
                    edtData.setError("Please enter data");
                    return; // Dừng chương trình
                }
                Toast.makeText(TestEventActivity.this, data, Toast.LENGTH_LONG).show();
                
                // Xử lý người dùng chọn quê
                int selectedId = radAddress.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                String address = radioButton.getText().toString();
                Toast.makeText(TestEventActivity.this, address, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Xử lý sự kiện checked cho checkbox
        cbBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mở khóa các thành phần UI khi checkbox được chọn
                    edtData.setEnabled(true);
                    btnClear.setEnabled(true);
                    btnClick.setEnabled(true);
                } else {
                    // Khóa các thành phần UI khi checkbox bỏ chọn
                    edtData.setEnabled(false);
                    btnClear.setEnabled(false);
                    btnClick.setEnabled(false);
                }
            }
        });
    }
}
