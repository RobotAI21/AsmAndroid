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

public class TestEventActivity extends AppCompatActivity {
    private EditText edtData;
    private Button btnClick, btnClear;
    private CheckBox cbBlock;
    private TextView tvTitle;
    private RadioGroup radAddress;
    private RadioButton radHN, radSL, radHG;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_event);
        // anh xa view
        edtData = findViewById(R.id.edtData);
        btnClick = findViewById(R.id.btnClick);
        btnClear = findViewById(R.id.btnClear);
        cbBlock = findViewById(R.id.cbBlock);
        tvTitle = findViewById(R.id.tvTitle);
        radAddress = findViewById(R.id.radAddress);
        radHN = findViewById(R.id.radHN);
        radSL = findViewById(R.id.radSL);
        radHG = findViewById(R.id.radHG);

        //block
        edtData.setEnabled(false);
        btnClear.setEnabled(false);
        btnClick.setEnabled(false);

        //su kien addText cho EditText
        edtData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim(); //lay noi dung nhap vao text
                tvTitle.setText(content);
            }
        });

        //bat su kien
        //su kien Onclick cho button
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtData.setText("");

            }
        });

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lay du lieu nguoi dung nhap vao
                String data = edtData.getText().toString().trim();
                if (TextUtils.isEmpty(data)) {
                    edtData.setError("Please enter data");
                    return; // dung chuong trinh
                }
                Toast.makeText(TestEventActivity.this, data, Toast.LENGTH_LONG).show();
                // xu ly nguoi dung chon que
                int selectedId = radAddress.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                String address = radioButton.getText().toString();
                Toast.makeText(TestEventActivity.this, address, Toast.LENGTH_SHORT).show();
            }
        });
        //su kien checked cho checkbox
        cbBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtData.setEnabled(true);
                    btnClear.setEnabled(true);
                    btnClick.setEnabled(true);
                } else {
                    edtData.setEnabled(false);
                    btnClear.setEnabled(false);
                    btnClick.setEnabled(false);
                }
            }
        });




    }
}
