package com.example.appdevelopment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.database.UserRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class SingUpActivity extends AppCompatActivity {
    Button btnRegister;
    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    TextView tvLogin;
    UserRepository repository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        repository = new UserRepository(SingUpActivity.this);
        btnRegister = findViewById(R.id.btnRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        tvLogin = findViewById(R.id.tvLogin);
        signupAccount();
    }
    private void signupAccount(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    edtUsername.setError("User name can't empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password can't empty");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email can't empty");
                    return;
                }
                //save account
                long insert = repository.saveUserAccount(username,password,email,phone);
                if(insert ==-1){
                    //fail
                    Toast.makeText(SingUpActivity.this, "Sing up fail", Toast.LENGTH_SHORT).show();
                }else{
                    //success
                    Toast.makeText(SingUpActivity.this, "Sing up successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    private void signupV1(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(username)){
                    edtUsername.setError("User name can't empty");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password can't empty");
                    return;
                }
                // save data user to file
                FileOutputStream fileOutput = null;
                try{
                    username = username += "|";
                    fileOutput = openFileOutput("account.txt", Context.MODE_APPEND); //Mode append ghi tiep sau file
                    fileOutput.write(username.getBytes(StandardCharsets.UTF_8));
                    fileOutput.write(password.getBytes(StandardCharsets.UTF_8));
                    fileOutput.write('\n');
                    fileOutput.close();
                    edtUsername.setText("");
                    edtPassword.setText("");
                    Toast.makeText(SingUpActivity.this, "Sing up successfully", Toast.LENGTH_SHORT).show();
                    // quay ve dang nhap
                    Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }
        });
    }
}
