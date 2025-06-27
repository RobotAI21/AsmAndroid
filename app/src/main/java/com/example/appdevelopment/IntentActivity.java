package com.example.appdevelopment;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class IntentActivity extends AppCompatActivity {
    Button btnLoad, btnCall, btnGo;
    EditText edtPhone, edtURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

        btnLoad = findViewById(R.id.btnLoad);
        edtURL = findViewById(R.id.edtUrl);
        btnCall = findViewById(R.id.btnCall);
        edtPhone = findViewById(R.id.edtPhone);
        btnGo = findViewById(R.id.btnGo);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lay url
                String url = edtURL.getText().toString().trim();
                if (TextUtils.isEmpty(url)){
                    edtURL.setError("URL NOT EMPTY");
                    return;
                }
                //load nd web tu url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edtPhone.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    edtPhone.setError("Phone number not empty");
                    return;
                }
                //call phone number
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                //check dieu kien goi
                if(ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                    startActivity(intent);
                }else {
                    //xin quyen call phone
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edtURL.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                //chuyen sang man hinh khac, gui ca du lieu sang man hinh khac
                Intent intent = new Intent(IntentActivity.this, DemoComponentActivity.class);
                Bundle bundle = new Bundle(); // dong goi tat ca cac tep tin thanh 1
                bundle.putString("MY_URL",url);
                bundle.putString("MY_PHONE",phone);
                bundle.putInt("ID_User", 929);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}
