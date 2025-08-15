package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import com.example.appdevelopment.adapters.ExpenseAdapter;
import com.example.appdevelopment.budgets.CreateExpenseActivity;
import com.example.appdevelopment.database.ExpenseModel;
import com.example.appdevelopment.database.ExpenseRepository;
import java.util.List;

/**
 * Fragment quản lý chi tiêu
 * Hiển thị danh sách chi tiêu và cho phép tạo chi tiêu mới
 */
public class ExpenseFragment extends Fragment {

    // Khai báo các thành phần UI
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    
    // Khai báo repository để thao tác với cơ sở dữ liệu chi tiêu
    private ExpenseRepository repository;
    
    // Danh sách chi tiêu
    private List<ExpenseModel> expenses;

    /**
     * Constructor mặc định của Fragment
     */
    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Phương thức tạo view cho Fragment
     * Thiết lập giao diện, animation và xử lý các sự kiện
     * @param inflater LayoutInflater để inflate layout
     * @param container ViewGroup chứa Fragment
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     * @return View đã được tạo
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        
        // Tải và áp dụng animation fade in
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(fadeIn);

        // Khởi tạo các thành phần UI
        recyclerView = view.findViewById(R.id.rvExpense);
        repository = new ExpenseRepository(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Xử lý sự kiện tạo chi tiêu mới
        Button btnCreate = view.findViewById(R.id.btnCreateExpense);
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
            
            // Lấy thông tin người dùng từ MainMenuActivity
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                activity.passUserInfoToActivity(intent);
            }
            
            startActivity(intent);
        });

        return view;
    }

    /**
     * Phương thức load dữ liệu chi tiêu
     * Tải danh sách chi tiêu của người dùng hiện tại
     */
    private void loadExpenses() {
        if (repository != null && recyclerView != null) {
            // Lấy thông tin người dùng từ MainMenuActivity
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                int userId = activity.getCurrentUserId();
                // Chỉ lấy chi tiêu của người dùng hiện tại
                expenses = repository.getExpensesByUserId(userId);
            } else {
                // Fallback: lấy tất cả chi tiêu nếu không có activity
                expenses = repository.getAllExpenses();
            }
            
            // Tạo adapter và gán cho RecyclerView
            adapter = new ExpenseAdapter(expenses);
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Phương thức được gọi khi Fragment được resume
     * Load lại dữ liệu để cập nhật giao diện
     */
    @Override
    public void onResume() {
        super.onResume();
        // Gọi hàm loadExpenses() mỗi khi fragment được hiển thị lại
        loadExpenses();
    }
}