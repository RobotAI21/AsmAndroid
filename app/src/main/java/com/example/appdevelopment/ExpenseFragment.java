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

public class ExpenseFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private ExpenseRepository repository;
    private List<ExpenseModel> expenses;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(fadeIn);

        recyclerView = view.findViewById(R.id.rvExpense);
        repository = new ExpenseRepository(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnCreate = view.findViewById(R.id.btnCreateExpense);
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateExpenseActivity.class);
            
            // Lấy thông tin user từ MainMenuActivity
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                activity.passUserInfoToActivity(intent);
            }
            
            startActivity(intent);
        });

        return view;
    }

    // Tải và làm mới dữ liệu
    private void loadExpenses() {
        if (repository != null && recyclerView != null) {
            // Lấy userId từ MainMenuActivity
            MainMenuActivity activity = (MainMenuActivity) getActivity();
            if (activity != null) {
                int userId = activity.getCurrentUserId();
                // Chỉ lấy expense của user hiện tại
                expenses = repository.getExpensesByUserId(userId);
            } else {
                // Fallback: lấy tất cả expense nếu không có activity
                expenses = repository.getAllExpenses();
            }
            adapter = new ExpenseAdapter(expenses);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Gọi hàm loadExpenses() mỗi khi fragment được hiển thị lại
        loadExpenses();
    }
}