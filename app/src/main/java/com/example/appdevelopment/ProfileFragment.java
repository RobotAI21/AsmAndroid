package com.example.appdevelopment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.SharedPreferences;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appdevelopment.database.DbHelper;

/**
 * Fragment quản lý hồ sơ người dùng
 * Cho phép xem thông tin cá nhân, thay đổi mật khẩu và đăng xuất
 */
public class ProfileFragment extends Fragment {
    // Khai báo các thành phần UI
    private EditText edtEmail, edtNewPassword;
    private Button btnVerifyEmail, btnChangePassword, btnLogout;
    private LinearLayout passwordSection;
    private DbHelper dbHelper;
    private TextView tvUsername, tvEmail, tvCreatedDate;

    // Tham số cho Fragment (không sử dụng)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    /**
     * Constructor mặc định của Fragment
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Phương thức factory để tạo instance mới của Fragment
     * @param param1 Tham số 1 (không sử dụng)
     * @param param2 Tham số 2 (không sử dụng)
     * @return Instance mới của ProfileFragment
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Phương thức khởi tạo Fragment
     * Lấy các tham số từ Bundle
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Phương thức tạo view cho Fragment
     * @param inflater LayoutInflater để inflate layout
     * @param container ViewGroup chứa Fragment
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     * @return View đã được tạo
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /**
     * Phương thức được gọi sau khi view được tạo
     * Thiết lập giao diện và xử lý các sự kiện
     * @param view View đã được tạo
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo các thành phần UI
        edtEmail = view.findViewById(R.id.edtEmail);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        btnVerifyEmail = view.findViewById(R.id.btnVerifyEmail);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        passwordSection = view.findViewById(R.id.passwordSection);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCreatedDate = view.findViewById(R.id.tvCreatedDate);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Khởi tạo database helper
        dbHelper = new DbHelper(requireContext());
        
        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // Load thông tin người dùng và hiển thị lên giao diện
        if (userId != -1) {
            Cursor cursor = dbHelper.getUserInfoById(userId);
            if (cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                tvUsername.setText("Username: " + username);
                tvEmail.setText("Email: " + email);
                tvCreatedDate.setText("Created Date: " + createdAt);
            }
            cursor.close();
        }

        // Ẩn phần thay đổi mật khẩu ban đầu
        passwordSection.setVisibility(View.GONE);

        // Xử lý sự kiện xác thực email
        btnVerifyEmail.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            // Kiểm tra tính hợp lệ của email
            if (email.isEmpty()) {
                edtEmail.setError("Please enter email");
                return;
            }

            // Kiểm tra email có tồn tại trong cơ sở dữ liệu không
            if (dbHelper.isEmailExists(email)) {
                Toast.makeText(getContext(), "Email verified. Please enter new password.", Toast.LENGTH_SHORT).show();
                passwordSection.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Email not found.", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện thay đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();

            // Kiểm tra độ dài mật khẩu mới
            if (newPassword.length() < 6) {
                edtNewPassword.setError("Password must be at least 6 characters");
                return;
            }

            // Cập nhật mật khẩu trong cơ sở dữ liệu
            if (dbHelper.updatePassword(email, newPassword)) {
                Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_LONG).show();
                requireActivity().getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new OverviewFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Xóa thông tin người dùng khỏi SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Chuyển về màn hình đăng nhập và xóa back stack
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}