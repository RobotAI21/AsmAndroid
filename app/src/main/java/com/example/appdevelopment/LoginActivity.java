package com.example.appdevelopment;

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

        tvRegister = findViewById(R.id.tvRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
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
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
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
    private void checkLoginInDataFile(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = edtUsername.getText().toString().trim();
                String Password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty("")){
                    edtUsername.setError("Enter Username, PLEASE!!!!!");
                    return;
                }
                if (TextUtils.isEmpty("")){
                    edtPassword.setError("Enter Password, PLEASE!!!!!");
                    return;
                }
                //Read data in file internal storage (account.txt)
                try {
                    FileInputStream fileInput = openFileInput("account.txt"); //open file internal
                    int read =-1;
                    StringBuilder builder = new StringBuilder();
                    while ((read =fileInput.read()) != -1){
                        builder.append((char)read);
                        //get all data from file gan vao builder
                    }
                    fileInput.close();
                    String[] userAccount = builder.toString().trim().split("\n"); //split: bien chuoi thanh mang
                    boolean checkLogin = false;
                    for(int i =0; i < userAccount.length; i++){
                        String user = userAccount[i].substring(0, userAccount[i].indexOf("|")); //subString: cat chuoi tu dau tien cuoi, indexOf: tim vi tri phan tu
                        String pass = userAccount[i].substring(userAccount[i].indexOf("|")+1);
                        if(Username.equals(user) && Password.equals(pass)){
                            checkLogin = true;
                            break;
                        }
                    }
                    if (checkLogin){
                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intent);
                        Bundle bundle = new Bundle();
                        //

                    }else{
                        Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
