package com.example.appdevelopment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.appdevelopment.adapters.BudgetAdapter;
import com.example.appdevelopment.budgets.CreateBudgetActivity;
import com.example.appdevelopment.database.BudgetModel;
import com.example.appdevelopment.database.BudgetRepository;
// Import ExpenseRepository để tính toán chi phí
import com.example.appdevelopment.database.ExpenseRepository;
import java.util.List;

/**
 * Fragment quản lý ngân sách
 * Hiển thị danh sách ngân sách và cho phép tạo ngân sách mới
 */
public class BudgetFragment extends Fragment {

    // Khai báo các thành phần UI
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    
    // Khai báo các repository để thao tác với cơ sở dữ liệu
    private BudgetRepository budgetRepository;
    private ExpenseRepository expenseRepository;
    
    // Danh sách ngân sách
    private List<BudgetModel> budgets;

    /**
     * Constructor mặc định của Fragment
     */
    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Phương thức tạo view cho Fragment
     * Thiết lập giao diện và xử lý các sự kiện
     * @param inflater LayoutInflater để inflate layout
     * @param container ViewGroup chứa Fragment
     * @param savedInstanceState Bundle chứa trạng thái trước đó
     * @return View đã được tạo
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        
        // Khởi tạo các thành phần UI
        Button btnCreate = view.findViewById(R.id.btnCreateBudget);
        recyclerView = view.findViewById(R.id.rvBudget);

        // Khởi tạo các repository để thao tác với cơ sở dữ liệu
        budgetRepository = new BudgetRepository(getContext());
        expenseRepository = new ExpenseRepository(getContext());

        // Thiết lập layout manager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Xử lý sự kiện tạo ngân sách mới
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetActivity.class);
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                // Truyền thông tin người dùng qua Intent
                activity.passUserInfoToActivity(intent);
            }
            startActivity(intent);
        });

        return view;
    }

    /**
     * Phương thức load dữ liệu ngân sách
     * Tải danh sách ngân sách và tính toán số tiền còn lại
     */
    private void loadBudgets() {
        if (budgetRepository != null && expenseRepository != null && recyclerView != null) {
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                // Lấy ID người dùng hiện tại
                int userId = activity.getCurrentUserId();
                // Lấy danh sách ngân sách của người dùng
                budgets = budgetRepository.getBudgetsByUserId(userId);

                // Tính toán số tiền còn lại cho mỗi ngân sách
                for (BudgetModel budget : budgets) {
                    // Lấy tổng chi tiêu cho ngân sách này
                    int totalExpenses = expenseRepository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), userId);
                    // Tính số tiền còn lại
                    int remainingMoney = budget.getMoneyBudget() - totalExpenses;
                    // Cập nhật vào model
                    budget.setRemainingMoney(remainingMoney);
                }

            } else {
                // Fallback: lấy tất cả ngân sách nếu không có thông tin người dùng
                budgets = budgetRepository.getAllBudgets();
            }
            
            // Tạo adapter và gán cho RecyclerView
            adapter = new BudgetAdapter(budgets);
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
        loadBudgets();
    }
}