package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.database.UserModel;
import com.example.appdevelopment.database.UserRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister;
    EditText edtUsername, edtPassword;
    Button btnLogin;
    UserRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        repository = new UserRepository(LoginActivity.this);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        tvRegister = findViewById(R.id.tvRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        findViewById(R.id.tvRegister).startAnimation(fadeIn);
        findViewById(R.id.edtUsername).startAnimation(slideUp);
        findViewById(R.id.edtPassword).startAnimation(slideUp);
        checkLoginWithDb();//xu ly dang nhap

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void checkLoginWithDb(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Enter Username, PLEASE!!!!!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Enter Password, PLEASE!!!!!");
                    return;
                }
                //check account in db
                UserModel infoAccount = repository.getInfoAccountByUsername(username ,password);
                assert infoAccount != null;
                if(infoAccount.getUsername() != null && infoAccount.getId()>0){
                    //login successfully
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID_ACCOUNT", infoAccount.getId());
                    bundle.putString("USERNAME_ACCOUNT", infoAccount.getUsername());
                    bundle.putString("EMAIL_ACCOUNT", infoAccount.getEmail());
                    bundle.putInt("ROLE_ACCOUNT", infoAccount.getRole());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish(); //ko back lai trang login
                }else {
                    //login fail
                    Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
