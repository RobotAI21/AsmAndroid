package com.example.appdevelopment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SharedReferencesActivity extends AppCompatActivity {
    Button btnSum, btnClear;
    EditText edtNumber1, edtNumber2, edtResult;
    TextView tvHistory;
    String history = ""; //du lieu de luu vao shared preferences
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_references);

        btnSum = findViewById(R.id.btnSum);
        btnClear = findViewById(R.id.btnClear);
        edtNumber1 = findViewById(R.id.edtNumber1);
        edtNumber2 = findViewById(R.id.edtNumber2);
        tvHistory = findViewById(R.id.tvHistory);
        edtResult = findViewById(R.id.edtResult);
        edtResult.setEnabled(false);

        //get data from shared
        SharedPreferences shared = getSharedPreferences("CalculatorPlus", MODE_PRIVATE);
        history = shared.getString("OPERATOR_PLUS", "");
        tvHistory.setText(history);
        //bat su kien
        btnSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long number1 = Integer.parseInt(edtNumber1.getText().toString().trim());
                long number2 = Integer.parseInt(edtNumber2.getText().toString().trim());
                long result = number1 + number2;
                edtResult.setText(String.valueOf(result));
                history += number1 + "+" + number2 + "=" + result;
                tvHistory.setText(history);
                history += "\n";
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history = "";
                tvHistory.setText("");
                edtNumber1.setText("");
                edtNumber2.setText("");
                edtResult.setText("");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // luu vao shared
        SharedPreferences myShared = getSharedPreferences("CalculatorPlus", MODE_PRIVATE);
        SharedPreferences.Editor editor = myShared.edit();
        editor.putString("OPERATOR_PLUS", history);
        editor.apply();

    }
}
