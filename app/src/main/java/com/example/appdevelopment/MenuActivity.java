package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    TextView tvAccount;
    Button btnLogout;
    Intent intent;
    Bundle bundle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tvAccount = findViewById(R.id.tvAccount);
        btnLogout = findViewById(R.id.logout);
        intent = getIntent();
        bundle = intent.getExtras();
        if(bundle == null){
            String username = bundle.getString("USERNAME_ACCOUNT", "");

        }

        //lay du lieu tu login truyen sang
        if (bundle != null){
            String username = bundle.getString("USERNAME_ACCOUNT", "");
            int idUser = bundle.getInt("ID_ACCOUNT", 0);
            String email = bundle.getString("EMAIL_ACCOUNT", "");
            tvAccount.setText(username);
        }else {
            Intent loginView = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(loginView);
            finish();
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove data in intent
                if(bundle !=null){
                    intent.removeExtra("USERNAME_ACCOUNT");
                    intent.removeExtra("ID_ACCOUNT");
                    intent.removeExtra("EMAIL_ACCOUNT");
                    intent.removeExtra("ROLE_ACCOUNT");
                }
                Intent login = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });
    }
}
