package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LifeActivity extends AppCompatActivity {

    private static final String LOG_ACTIVITY ="logActivity";
    Button btnNextActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ham nay la noi khai bao cac bien hay load layout giao dien
        // ham nay se chay ngay khi co mot activity duoc kich hoat
        setContentView(R.layout.activity_life);
        Log.i(LOG_ACTIVITY, "************ onCreate ************");

        //anh xa View: tim phan tu ngoai view qua ID
        btnNextActivity = findViewById(R.id.btnNext);
        // bat su kien cho btn - khi nguoi dung click
        btnNextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LifeActivity.this, "Hello Nice To Meet You", Toast.LENGTH_SHORT).show();
                // chuyen activity khac
                Intent intent = new Intent(LifeActivity.this, LifeSecondActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ham duoc goi truoc khi Activity duoc hien thi
        Log.i(LOG_ACTIVITY, "************ onStart ************");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ham chay khi Activity co the tuong tac voi ng dung
        Log.i(LOG_ACTIVITY, "************ onResume ************");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //ham se duoc goi khi co 1 activity moi sap duoc kich hoat
        Log.i(LOG_ACTIVITY, "************ onPause ************");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ham duoc goi khi Activity bi an di
        Log.i(LOG_ACTIVITY, "************ onStop ************");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //ham chay khi 1 activity di tung bi an di va hien thi lai
        //keo theo OnResume va OnStart chay lai
        Log.i(LOG_ACTIVITY, "************ onRestart ************");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ham chay khi 1 activity bi huy
        Log.i(LOG_ACTIVITY, "************ onDestroy ************");
    }
}
