package com.example.appdevelopment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Year;
import java.util.Calendar;

public class DemoComponentActivity extends AppCompatActivity {
    Button btnExit;
    EditText edtDate;

    TextView tvUserId, tvUrl, tvPhone;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_component);

        btnExit = findViewById(R.id.btnExit);
        edtDate = findViewById(R.id.edtDate);
        tvUserId = findViewById(R.id.tvUserId);
        tvUrl = findViewById(R.id.tvUrl);
        tvPhone = findViewById(R.id.tvPhone);
        //get data from DemoIntent Activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String url = bundle.getString("MY_URL", "");
            String phone = bundle.getString("MY_PHONE", "");
            int id = bundle.getInt("ID_User", 0);
            tvUserId.setText("UserId: "+ String.valueOf(id));
            tvUrl.setText("URL: "+ url);
            tvPhone.setText("Phone: "+ phone);
        };


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tao 1 hop thoai Alert Dialog
                AlertDialog.Builder alertExit = new AlertDialog.Builder(DemoComponentActivity.this);
               alertExit.setMessage("Do you want to exit?");
               alertExit.setTitle("ALERT!!!!!!");
               alertExit.setCancelable(false); //muon dong bat buoc phai click button
               alertExit.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                   // buttin yes - thoat app
                   finish(); // dong app
               });
               alertExit.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                   dialog.cancel();//ko lam gi ca
                   //button no
               });
               AlertDialog alertDialog = alertExit.create();
               alertDialog.show();
            }
        });

        //date time
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int years = calendar.get(Calendar.YEAR);
                int months = calendar.get(Calendar.MONTH);
                int days = calendar.get(Calendar.DAY_OF_MONTH);
                //tao date picker dialog
                DatePickerDialog datePicker = new DatePickerDialog(
                        DemoComponentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "-"+ (month +1)+ "-" + year;
                        edtDate.setText(date);
                    }
                }, years, months, days);
                datePicker.show();
            }
        });



    }
}
