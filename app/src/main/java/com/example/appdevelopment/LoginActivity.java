package com.example.appdevelopment;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdevelopment.database.UserModel;
import com.example.appdevelopment.database.UserRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    TextView tvRegister, tvForgotPassword;
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
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

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
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userId", infoAccount.getId());
                    editor.putString("username", infoAccount.getUsername());
                    editor.putString("email", infoAccount.getEmail());
                    editor.putString("created_at", infoAccount.getCreatedAt()); // đảm bảo có getCreatedAt()
                    editor.apply();


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
    private void showForgotPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);

        EditText edtUsername = dialogView.findViewById(R.id.edtUsernameForgot);
        EditText edtOldPassword = dialogView.findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edtConfirmPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password")
                .setView(dialogView)
                .setPositiveButton("Submit", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button btnSubmit = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSubmit.setOnClickListener(view -> {
                String username = edtUsername.getText().toString().trim();
                String oldPassword = edtOldPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(oldPassword)
                        || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserModel user = repository.getUserByUsername(username);
                if (user != null && user.getPassword().equals(oldPassword)) {
                    boolean updated = repository.updatePassword(user.getId(), newPassword);
                    if (updated) {
                        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();

                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", user.getId());
                        editor.putString("username", user.getUsername());
                        editor.putString("email", user.getEmail());
                        editor.putString("created_at", user.getCreatedAt());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

}
