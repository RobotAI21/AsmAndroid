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
// SỬA: Import ExpenseRepository để tính toán chi phí
import com.example.appdevelopment.database.ExpenseRepository;
import java.util.List;

public class BudgetFragment extends Fragment {

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private BudgetRepository budgetRepository;
    // SỬA: Thêm ExpenseRepository
    private ExpenseRepository expenseRepository;
    private List<BudgetModel> budgets;

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        Button btnCreate = view.findViewById(R.id.btnCreateBudget);
        recyclerView = view.findViewById(R.id.rvBudget);

        // SỬA: Khởi tạo cả hai repository
        budgetRepository = new BudgetRepository(getContext());
        expenseRepository = new ExpenseRepository(getContext()); // Khởi tạo repo chi tiêu

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateBudgetActivity.class);
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                activity.passUserInfoToActivity(intent);
            }
            startActivity(intent);
        });

        return view;
    }

    // SỬA: Tải, tính toán và làm mới dữ liệu
    private void loadBudgets() {
        if (budgetRepository != null && expenseRepository != null && recyclerView != null) {
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                int userId = activity.getCurrentUserId();
                budgets = budgetRepository.getBudgetsByUserId(userId);

                // SỬA (QUAN TRỌNG): Tính toán số tiền còn lại cho mỗi budget
                for (BudgetModel budget : budgets) {
                    // Lấy tổng chi tiêu cho budget này
                    int totalExpenses = expenseRepository.getTotalMonthlyExpensesByBudgetAndUser(budget.getId(), userId);
                    // Tính số tiền còn lại
                    int remainingMoney = budget.getMoneyBudget() - totalExpenses;
                    // Cập nhật vào model
                    budget.setRemainingMoney(remainingMoney);
                }

            } else {
                budgets = budgetRepository.getAllBudgets(); // Fallback
            }
            adapter = new BudgetAdapter(budgets);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBudgets();
    }
}