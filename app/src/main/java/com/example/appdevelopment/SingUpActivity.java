package com.example.appdevelopment;

import android.content.Context;
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

import com.example.appdevelopment.database.UserRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Activity đăng ký tài khoản mới
 * Cho phép người dùng tạo tài khoản với thông tin cá nhân
 */
public class SingUpActivity extends AppCompatActivity {
    // Khai báo các thành phần UI
    Button btnRegister;
    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    TextView tvLogin;
    UserRepository repository;
    
    /**
     * Phương thức khởi tạo Activity
     * Thiết lập giao diện, animation và xử lý các sự kiện đăng ký
     * @param savedInstanceState Bundle chứa trạng thái trước đó của Activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        // Tải các animation cho giao diện
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);

        // Áp dụng animation cho các thành phần UI
        findViewById(R.id.btnRegister).startAnimation(fadeIn);
        findViewById(R.id.edtUsername).startAnimation(slideUp);
        findViewById(R.id.edtPassword).startAnimation(slideUp);
        findViewById(R.id.edtEmail).startAnimation(slideUp);
        findViewById(R.id.edtPhone).startAnimation(slideUp);
        findViewById(R.id.tvLogin).startAnimation(zoomIn);

        // Khởi tạo repository để thao tác với cơ sở dữ liệu
        repository = new UserRepository(SingUpActivity.this);
        
        // Khởi tạo các thành phần UI
        btnRegister = findViewById(R.id.btnRegister);
        edtUsername = findViewById(R.id.edtUsername);
        
        // Kiểm tra và áp dụng animation shake nếu trường username trống
        if (edtUsername.getText().toString().isEmpty()) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            edtUsername.startAnimation(shake);
        }
        
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        tvLogin = findViewById(R.id.tvLogin);
        
        // Thiết lập các xử lý sự kiện
        signupAccount();
        backLogin();
    }
    
    /**
     * Phương thức xử lý sự kiện quay lại trang đăng nhập
     * Chuyển hướng từ trang đăng ký về trang đăng nhập
     */
    private void backLogin(){
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    
    /**
     * Phương thức xử lý đăng ký tài khoản
     * Kiểm tra thông tin và lưu tài khoản mới vào cơ sở dữ liệu
     */
    private void signupAccount(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin từ form đăng ký
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                // Kiểm tra tính hợp lệ của dữ liệu nhập
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
                
                // Lưu tài khoản vào cơ sở dữ liệu
                long insert = repository.saveUserAccount(username,password,email,phone);
                if(insert ==-1){
                    // Hiển thị thông báo đăng ký thất bại
                    Toast.makeText(SingUpActivity.this, "Sing up fail", Toast.LENGTH_SHORT).show();
                }else{
                    // Hiển thị thông báo đăng ký thành công và chuyển về trang đăng nhập
                    Toast.makeText(SingUpActivity.this, "Sing up successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
